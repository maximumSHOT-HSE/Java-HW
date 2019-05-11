package ru.hse.surkov.hw07;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class ConcurrentQueueTest {

    @Test
    void testSingleThread() throws InterruptedException {
        ConcurrentQueue<String> queue = new ConcurrentQueue<>();
        int l, r;
        for (l = 0, r = 0; r < 100; r++) {
            queue.push(Integer.toString(r));
            while (r - l + 1 > 5) {
                assertEquals(Integer.toString(l), queue.pop());
                l++;
            }
        }
        while (l < r) {
            assertEquals(Integer.toString(l), queue.pop());
            l++;
        }
    }

    private class Counter {
        private int count;

        public int getCount() {
            return count;
        }

        public void increment() {
            count++;
        }
    }

    @RepeatedTest(100)
    void testPop() throws InterruptedException {
        final ConcurrentQueue<Set<Integer>> queue = new ConcurrentQueue<>();
        Thread[] threads = new Thread[10];
        final var counter = new Counter();
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int i1 = 0; i1 < 100; i1++) {
                    for (int j = 0; j < 2; j++) {
                        queue.push(new TreeSet<>());
                    }
                    for (int j = 0; j < 2; j++) {
                        Set<Integer> set = null;
                        try {
                            set = queue.pop();
                        } catch (InterruptedException e) {
                            counter.increment();
                        }
                        if (set == null || !set.isEmpty()) {
                            counter.increment();
                        } else {
                            set.add(5);
                        }
                    }
                }
            });
        }
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
        assertEquals(0, counter.getCount());
    }

    @RepeatedTest(100)
    void testPush() throws InterruptedException {
        final ConcurrentQueue<Integer> queue = new ConcurrentQueue<>();
        Thread[] threads = new Thread[10];
        final var counter = new Counter();
        for (int i = 0; i < 10; i++) {
            final int I = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    queue.push(j);
                }
                for (int j = 0; j < 100; j++) {
                    try {
                        queue.pop();
                    } catch (InterruptedException e) {
                        counter.increment();
                    }
                }
            });
        }
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }
        assertEquals(0, counter.getCount());
    }
}