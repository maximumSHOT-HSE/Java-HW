package ru.hse.surkov.hw07;

import java.util.LinkedList;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;

public class ConcurrentQueue<T> {

    @NotNull private Queue<T> queue = new LinkedList<>();

    public synchronized void push(@NotNull T element) {
        queue.add(element);
        if (queue.size() == 1) {
            notifyAll();
        }
    }

    @NotNull public synchronized T pop() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }
}
