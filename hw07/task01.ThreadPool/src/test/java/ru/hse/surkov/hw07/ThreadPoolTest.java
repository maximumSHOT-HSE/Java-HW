package ru.hse.surkov.hw07;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadPoolTest {

    private static final int N = 20;
    @NotNull private ThreadPool pool = new ThreadPool(Runtime.getRuntime().availableProcessors());

    @RepeatedTest(10)
    void testBasics() throws LightExecutionException {
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            final int I = i;
            tasks.add(pool.addTask(() -> I));
        }
        while (true) {
            int any = 0;
            for (var task : tasks) {
                if (!task.isReady()) {
                    any = 1;
                }
            }
            if (any == 0) {
                break;
            }
        }
        for (int i = 0; i < N; i++) {
            assertEquals(Optional.of(i), Optional.ofNullable(tasks.get(i).get()));
        }
    }

    @RepeatedTest(10)
    void testBasicThenApply() throws LightExecutionException {
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            final int I = i;
            LightFuture<Integer> task = pool.addTask(() -> {
//                System.out.println("DONE " + (I + 5));
                return I + 5;
            });
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tasks.add(task.thenApply(x -> {
//                System.out.println("DONE " + (x * x));
                return x * x;
            }));
        }
        while (true) {
            int any = 0;
            for (var task : tasks) {
                if (!task.isReady()) {
                    any = 1;
                }
            }
            if (any == 0) {
                break;
            }
        }
        for (int i = 0; i < N; i++) {
            assertEquals(Optional.of((i + 5) * (i + 5)), Optional.ofNullable(tasks.get(i).get()));
        }
    }
}