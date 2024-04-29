package com.ezerium.http;

import com.ezerium.annotations.Async;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@RequiredArgsConstructor
@AllArgsConstructor
public class HTTPRequest {

    private final String url;
    private final Map<String, String> headers = new HashMap<>();
    private String method = "GET";


    public void header(String key, String value) {
        this.headers.put(key, value);
    }

    public void method(String method) {
        this.method = method;
    }

    @Async
    public void send() throws Exception {
        HttpURLConnection connection = this.setup();
        connection.disconnect();
    }

    private HttpURLConnection setup() throws Exception {
        URL url = new URL(this.url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(this.method);
        for (Map.Entry<String, String> entry : this.headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        connection.connect();
        if (connection.getResponseCode() != 200) {
            throw new Exception("Failed to send request: " + connection.getResponseCode());
        }

        return connection;
    }

    public JsonObject fetch() throws Exception {
        return fetch(false);
    }

    public void fetch(Consumer<JsonObject> consumer) {
        new Thread(() -> {
            try {
                consumer.accept(fetch());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public JsonObject fetch(boolean async) throws Exception {
        if (async) return CompletableFuture.supplyAsync(() -> {
            try {
                return this.fetch(false);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }).get();

        HttpURLConnection connection = this.setup();

        InputStream stream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        connection.disconnect();
        return (JsonObject) new JsonParser().parse(builder.toString());
    }

}
