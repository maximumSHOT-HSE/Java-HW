package ru.hse.surkov.hw07;

import java.util.LinkedList;
import java.util.Queue;

import org.jetbrains.annotations.NotNull;

/**
 * Blocking queue with a guarantee of a correct work
 * in a concurrent mode with several threads.
 * */
public class ConcurrentQueue<T> {

    @NotNull private Queue<T> queue = new LinkedList<>();

    /**
     * Adds element into the tail of queue.
     *
     * @param element, which should be added
     * */
    public synchronized void push(@NotNull T element) {
        queue.add(element);
        if (queue.size() == 1) {
            notifyAll();
        }
    }

    /**
     * Waits until queue will be a non-empty, then
     * retrieves element from queue head, deletes and returns it.
     *
     * @return retrieved element
     * */
    @NotNull public synchronized T pop() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }
}
