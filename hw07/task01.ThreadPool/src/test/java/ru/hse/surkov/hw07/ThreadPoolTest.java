package ru.hse.surkov.hw07;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {

    private static final int N = 20;
    private ThreadPool pool;

    @BeforeEach
    void setUp() {
        pool = new ThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @RepeatedTest(5)
    void testBasics() throws LightExecutionException {
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            final int I = i;
            tasks.add(pool.submit(() -> I));
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

    @RepeatedTest(5)
    void testBasicThenApply() throws LightExecutionException {
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            final int I = i;
            LightFuture<Integer> task = pool.submit(() -> I + 5);
            tasks.add(task.thenApply(x -> x * x));
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

    @RepeatedTest(5)
    void testSimpleException() {
        List<LightFuture<String>> tasks = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            LightFuture<String> task = pool.submit(() -> {
                throw new IllegalStateException("illegal state");
            });
            tasks.add(task);
        }
        for (var task : tasks) {
            assertThrows(LightExecutionException.class, task::get);
        }
    }

    @RepeatedTest(5)
    void testThenApplyException() {
        List<LightFuture<Integer>> firstTasks = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            final int I = i;
            LightFuture<Integer> task = pool.submit(() -> I);
            firstTasks.add(task);
        }
        List<LightFuture<Integer>> secondTasks = new ArrayList<>();
        for (var firstTask : firstTasks) {
            LightFuture<Integer> secondTask = firstTask.thenApply(x -> (x + 5) / x);
            secondTasks.add(secondTask);
        }
        while (true) {
            int any = 0;
            for (var task : secondTasks) {
                if (!task.isReady()) {
                    any = 1;
                }
            }
            if (any == 0) {
                break;
            }
        }
        for (int i = 0; i < secondTasks.size(); i++) {
            if (i == 0) {
                assertThrows(LightExecutionException.class, secondTasks.get(i)::get);
            } else {
                Integer result = null;
                try {
                    result = secondTasks.get(i).get();
                } catch (LightExecutionException e) {
                    fail();
                }
                if (result == null) {
                    fail();
                }
                assertEquals((i + 5) / i, (int) result);
            }
        }
        for (int i = 0; i < firstTasks.size(); i++) {
            Integer result = null;
            try {
                result = firstTasks.get(i).get();
            } catch (LightExecutionException e) {
                fail();
            }
            if (result == null) {
                fail();
            }
            assertEquals(i, (int) result);
        }
    }

    @RepeatedTest(5)
    void testThrowParentException() {
        for (int numberOfThreads = 1; numberOfThreads <= 10; numberOfThreads++) {
            pool = new ThreadPool(numberOfThreads);
            List<LightFuture<Integer>> firstTasks = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                LightFuture<Integer> task = pool.submit(() -> {
                    throw new IllegalStateException(":(");
                });
                firstTasks.add(task);
            }
            List<LightFuture<Integer>> secondTasks = new ArrayList<>();
            for (var firstTask : firstTasks) {
                LightFuture<Integer> secondTask = firstTask.thenApply(x -> x + 1);
                secondTasks.add(secondTask);
            }
            while (true) {
                int any = 0;
                for (var task : secondTasks) {
                    if (!task.isReady()) {
                        any = 1;
                    }
                }
                if (any == 0) {
                    break;
                }
            }
            for (var task : firstTasks) {
                assertTrue(task.isReady());
            }
            for (var task : secondTasks) {
                assertTrue(task.isReady());
                assertThrows(LightExecutionException.class, task::get);
            }
            for (var task : firstTasks) {
                assertThrows(LightExecutionException.class, task::get);
            }
        }
    }

    @RepeatedTest(5)
    void testFactorial() throws LightExecutionException {
        List<LightFuture<BigInteger>> tasks = new ArrayList<>();
        LightFuture<BigInteger> currentTask = pool.submit(() -> BigInteger.ONE);
        tasks.add(currentTask);
        for (int i = 1; i <= N; i++) {
            final BigInteger I = new BigInteger(Integer.toString(i));
            currentTask = currentTask.thenApply(x -> x.multiply(I));
            tasks.add(currentTask);
        }
        while (!tasks.get(N).isReady());
        BigInteger current = BigInteger.ONE;
        for (int i = 0; i <= N; i++) {
            assertTrue(tasks.get(i).isReady());
            assertEquals(current, tasks.get(i).get());
            current = current.multiply(new BigInteger(Integer.toString(i + 1)));
        }
    }

    @RepeatedTest(5)
    void testBasicShutdown() {
        LightFuture<Integer> task = pool.submit(() -> {
            int x = 1;
            while (x == 1) {
            }
            return 1;
        });
        pool.shutdown();
        assertFalse(task.isReady());
    }

    @RepeatedTest(5)
    void testLongChainAndNormalTasks() throws LightExecutionException {
        for (int numberOfThreads = 2; numberOfThreads <= 10; numberOfThreads++) {
            pool = new ThreadPool(numberOfThreads);

            List<LightFuture<Integer>> chain = new ArrayList<>();
            LightFuture<Integer> root = pool.submit(() -> {
                boolean flag = true;
                while (flag) {

                }
                return 0;
            });

            chain.add(root);

            for (int i = 0; i < 10; i++) {
                root = root.thenApply(x -> x + 1);
                chain.add(root);
            }

            List<LightFuture<Integer>> tasks = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                final int I = i;
                tasks.add(pool.submit(() -> I * I * I));
            }

            while (!tasks.stream().allMatch(LightFuture::isReady));

            for (var task : tasks) {
                assertTrue(task.isReady());
            }

            for (int i = 0; i < 10; i++) {
                Integer result = tasks.get(i).get();
                assertEquals(i * i * i, (int) result);
            }

            pool.shutdown();

            for (var vertex : chain) {
                assertFalse(vertex.isReady());
            }
        }
    }

    @RepeatedTest(5)
    void testLowerBoundOfThreadNumber() {
        for (int T = 1; T <= 10; T++) {
            int before = Thread.activeCount();
            pool = new ThreadPool(T);
            int after = Thread.activeCount();
            assertTrue(after - before >= T);
//            System.out.println(after - before + " : " + T);
        }
    }

    @Test
    void testIllegalNumberOfThreads() {
        for (int numberOfThreads = -10; numberOfThreads <= 0; numberOfThreads++) {
            final int helper = numberOfThreads;
            assertThrows(IllegalArgumentException.class, () -> pool = new ThreadPool(helper));
        }
    }
}