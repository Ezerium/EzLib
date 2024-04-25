package com.ezerium.spigot.queue;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class QueueHandler<T> {

    private final Map<String, Queue<T>> queues;

    public QueueHandler() {
        this.queues = new HashMap<>();
    }

    public void createQueue(String name, int size, Consumer<T> execution) {
        queues.put(name, new Queue<>(size, execution));
    }

    public void createQueue(String name, Consumer<T> execution) {
        queues.put(name, new Queue<>(execution));
    }

    public void deleteQueue(String name) {
        queues.remove(name);
    }

    public Queue<T> getQueue(String name) {
        return queues.get(name);
    }

    public boolean queueExists(String name) {
        return queues.containsKey(name);
    }

    public T getNext(String name) {
        return queues.get(name).next();
    }

}
