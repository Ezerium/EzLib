package com.ezerium.spigot.queue;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Data
public class Queue<T> {

    private final List<T> queue = new ArrayList<>();
    private final int maxSize;
    private final Consumer<T> execution;

    public Queue(int size, Consumer<T> execution) {
        this.maxSize = size;
        this.execution = execution;
    }

    public Queue(Consumer<T> execution) {
        this(-1, execution);
    }

    public int getPosition(T t) {
        return queue.indexOf(t) + 1;
    }

    public int getSize() {
        return queue.size();
    }

    public T get(int index) {
        return queue.get(index);
    }

    public boolean inQueue(T t) {
        return queue.contains(t);
    }

    public void add(T t) {
        queue.add(t);
    }

    public void remove(T t) {
        queue.remove(t);
    }

    public void remove() {
        this.move();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean isFull() {
        return maxSize != -1 && queue.size() >= maxSize;
    }

    public T next() {
        return queue.get(0);
    }

    public T move() {
        return queue.remove(0);
    }

    public T sendNext() {
        T t = move();
        execution.accept(t);
        return t;
    }

}
