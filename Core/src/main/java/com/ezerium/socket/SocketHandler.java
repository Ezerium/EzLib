package com.ezerium.socket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

@Data
@AllArgsConstructor
public class SocketHandler {

    private final EzServer server;
    private final List<EzSocketListener> listeners;
    private boolean running;

    private ServerSocket socket;

    public void start() {
        server.onStart();

        this.running = true;
        this.listen();
    }

    public void send(EzSocket socket) {

    }

    @SneakyThrows
    private void listen() {
        this.socket = new ServerSocket(server.getPort());
        while (running) {
            Socket client = socket.accept();


        }
    }

    public void stop() {
        this.running = false;
        this.server.onStop();
    }

    public void addListener(EzSocketListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(EzSocketListener listener) {
        this.listeners.remove(listener);
    }

    public void clearListeners() {
        this.listeners.clear();
    }

}
