package com.ezerium.rabbitmq;

import com.ezerium.annotations.Async;
import com.ezerium.utils.LoggerUtil;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MQHandler {

    private final List<MQListener> registeredListeners;
    private final List<String> registeredChannels;
    private final Map<String, JsonObject> awaitingResponse;

    private final Channel channel;
    private boolean running = false;
    private Thread listenerThread;

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String vhost;
    private final String channelName;

    public MQHandler(String host, int port, String username, String password, String channel) {
        this(host, port, username, password, "/", channel);
    }

    public MQHandler(String host, int port, String channel) {
        this(host, port, "guest", "guest", channel);
    }

    public MQHandler(String host, String channel) {
        this(host, 5672, channel);
    }

    public MQHandler(String host, String username, String password, String channel) {
        this(host, 5672, username, password, channel);
    }

    public MQHandler(String host, String username, String password, String vhost, String channel) {
        this(host, 5672, username, password, vhost, channel);
    }

    public MQHandler(String host, String vhost, String channel) {
        this(host, 5672, "guest", "guest", vhost, channel);
    }

    @SneakyThrows
    public MQHandler(String host, int port, String username, String password, String vhost, String channel) {
        this.registeredListeners = new ArrayList<>();
        this.registeredChannels = new ArrayList<>();
        this.awaitingResponse = new HashMap<>();

        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.vhost = vhost;
        this.channelName = channel;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(vhost);

        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        this.listen();
    }

    public void listen() throws Exception {
        Validate.isTrue(!this.running, "Cannot execute MQHandler#listen as it's already running.");

        this.running = true;
        this.channel.queueDeclare(this.channelName, false, false, false, null);
        listenerThread = new Thread(() -> {
            try {
                this.channel.basicConsume(this.channelName, true, (consumerTag, delivery) -> {
                    String message = new String(delivery.getBody());
                    JsonObject body;
                    try {
                        body = (JsonObject) new JsonParser().parse(message);
                    } catch (Exception e) {
                        LoggerUtil.warn("Failed to parse message: '" + message + "' - Message is not a valid JSON object, ignoring.");
                        return;
                    }

                    for (MQListener listener : registeredListeners) {
                        this.handleListener(listener, body);
                    }
                }, consumerTag -> {});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        listenerThread.start();
    }

    private void handleListener(MQListener listener, JsonObject body) {
        Method[] methods = listener.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MQPacket.class)) {
                MQPacket annotation = method.getAnnotation(MQPacket.class);
                JsonElement packetNameElement = body.get("ezIdPacketName");
                if (packetNameElement == null || packetNameElement.isJsonNull()) {
                    LoggerUtil.warn("Received a packet without a packet name, ignoring.");
                    return;
                }

                String packetName = packetNameElement.getAsString();
                if (Arrays.asList(annotation.value()).contains(packetName)) {
                    if (body.has("isResponse")) {
                        String returnId = body.get("ezReturnId").getAsString();
                        this.awaitingResponse.put(returnId, body);
                        return;
                    }

                    if (body.has("ezReturnTo")) {
                        String returnTo = body.get("ezReturnTo").getAsString();
                        if (!method.getReturnType().getName().equals("com.google.gson.JsonObject")) {
                            LoggerUtil.warn("Cannot return a non-JsonObject return type for a packet that expects a response, ignoring.");
                            return;
                        }

                        try {
                            JsonObject response = (JsonObject) method.invoke(listener, body);
                            response.addProperty("ezReturnId", body.get("ezReturnId").getAsString());
                            response.addProperty("isResponse", true);
                            this.send(returnTo, MQData.create(returnTo, response));
                        } catch (Exception e) {
                            LoggerUtil.err("An error occurred while invoking method " + method.getName() + " for listener " + listener.getClass().getSimpleName() + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            method.invoke(listener, body);
                        } catch (Exception e) {
                            LoggerUtil.err("An error occurred while invoking method " + method.getName() + " for listener " + listener.getClass().getSimpleName() + ": " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void send(MQData data) {
        this.send(data, true);
    }

    public void send(MQData data, boolean sendSelf) {
        if (sendSelf) this.send(this.channelName, data);

        for (String channel : this.registeredChannels) {
            this.send(channel, data);
        }
    }

    @Async
    public void send(String channel, MQData data) {
        if(!channel.equals(this.channelName)) Preconditions.checkArgument(this.registeredChannels.contains(channel), "Cannot send a message to an unregistered channel.");
        try {
            data.getData().addProperty("ezIdPacketName", data.getName());
            this.channel.basicPublish("", channel, null, data.getData().toString().getBytes());
        } catch (IOException e) {
            LoggerUtil.err("An error occurred while sending a message to channel " + channel + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private CompletableFuture<JsonObject> getAsync(String channel, MQData data) {
        return CompletableFuture.supplyAsync(() -> this.get(channel, data));
    }

    private CompletableFuture<JsonObject> getAsync(String channel, MQData data, long timeout) {
        return CompletableFuture.supplyAsync(() -> this.get(channel, data, timeout));
    }

    @Nullable
    public JsonObject get(String channel, MQData data, long timeout) {
        Preconditions.checkArgument(this.registeredChannels.contains(channel), "Cannot get a message from an unregistered channel.");
        data.getData().addProperty("ezIdPacketName", data.getName());

        String returnId = UUID.randomUUID().toString().split("-")[0];
        data.getData().addProperty("ezReturnTo", this.channelName);
        data.getData().addProperty("ezReturnId", returnId);

        this.send(channel, data);

        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeout) {
            if (this.awaitingResponse.containsKey(returnId)) {
                JsonObject response = this.awaitingResponse.get(returnId);
                this.awaitingResponse.remove(returnId);
                return response;
            }
        }

        LoggerUtil.err("Failed to get a response from channel " + channel + " within the timeout of " + timeout + "ms.");
        return null;
    }

    @Nullable
    public JsonObject get(String channel, MQData data) {
        return this.get(channel, data, 10000);
    }

    public void registerListener(@NotNull MQListener listener) {
        Validate.notNull(listener, "Cannot register a null listener.");
        Validate.isTrue(!this.registeredListeners.contains(listener), "Cannot register the same listener twice.");

        this.registeredListeners.add(listener);
    }

    public void registerChannel(@NotNull String channel) {
        Validate.notNull(channel, "Cannot register a null channel.");
        Validate.isTrue(!this.registeredChannels.contains(channel), "Cannot register the same channel twice.");

        this.registeredChannels.add(channel);
    }

    public void stop() {
        Validate.isTrue(this.running, "Cannot execute MQHandler#stop as it's not running.");

        this.running = false;
        listenerThread.interrupt();
    }

}
