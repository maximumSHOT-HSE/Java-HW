package ru.hse.surkov.hw07;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {

    private static final int TASKS_NUMBER = 20;
    private ThreadPool pool;

    @BeforeEach
    void setUp() {
        pool = new ThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @RepeatedTest(5)
    void testBasics() throws LightExecutionException {
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < TASKS_NUMBER; i++) {
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
        for (int i = 0; i < TASKS_NUMBER; i++) {
            assertEquals(Optional.of(i), Optional.ofNullable(tasks.get(i).get()));
        }
    }

    @RepeatedTest(5)
    void testBasicThenApply() throws LightExecutionException {
        List<LightFuture<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < TASKS_NUMBER; i++) {
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
        for (int i = 0; i < TASKS_NUMBER; i++) {
            assertEquals(Optional.of((i + 5) * (i + 5)), Optional.ofNullable(tasks.get(i).get()));
        }
    }

    @RepeatedTest(5)
    void testSimpleException() {
        List<LightFuture<String>> tasks = new ArrayList<>();
        for (int i = 0; i < TASKS_NUMBER; i++) {
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
        for (int i = 0; i < TASKS_NUMBER; i++) {
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
            for (int i = 0; i < TASKS_NUMBER; i++) {
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
        for (int i = 1; i <= TASKS_NUMBER; i++) {
            final BigInteger I = new BigInteger(Integer.toString(i));
            currentTask = currentTask.thenApply(x -> x.multiply(I));
            tasks.add(currentTask);
        }
        while (!tasks.get(TASKS_NUMBER).isReady());
        BigInteger current = BigInteger.ONE;
        for (int i = 0; i <= TASKS_NUMBER; i++) {
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
        for (int T = 95; T <= 100; T++) {
            int before = Thread.activeCount();
            pool = new ThreadPool(T);
            for (int i = 0; i < 5; i++) {
                pool.submit(() -> 5);
            }
            int after = Thread.activeCount();
            assertTrue(after - before >= T);
        }
    }

    @Test
    void testIllegalNumberOfThreads() {
        for (int numberOfThreads = -10; numberOfThreads <= 0; numberOfThreads++) {
            final int helper = numberOfThreads;
            assertThrows(IllegalArgumentException.class, () -> pool = new ThreadPool(helper));
        }
    }

    private class Holder {
        int x = 5;

        public Holder() {

        }

        public Holder(int i) {
            x = i;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof Holder && x == ((Holder) obj).x;
        }
    }

    @Test
    void testDifferentTasksTypes() throws LightExecutionException {
        LightFuture<Integer> integerLightFuture = pool.submit(() -> 5);
        LightFuture<Double> doubleLightFuture = pool.submit(() -> 0.5);
        LightFuture<String> stringLightFuture = pool.submit(() -> "5");
        LightFuture<Holder> holderLightFuture = pool.submit(Holder::new);
        List<LightFuture<?>> tasks = new ArrayList<>();
        tasks.add(integerLightFuture);
        tasks.add(doubleLightFuture);
        tasks.add(stringLightFuture);
        tasks.add(holderLightFuture);
        while (true) {
            int all = 1;
            for (var task : tasks) {
                if (!task.isReady()) {
                    all = 0;
                    break;
                }
            }
            if (all == 1) {
                break;
            }
        }
        for (var task : tasks) {
            assertTrue(task.isReady());
        }
        assertEquals(5, (int) integerLightFuture.get());
        assertEquals(0.5, (double) doubleLightFuture.get());
        assertEquals("5", stringLightFuture.get());
        assertNotEquals(new Holder(6), holderLightFuture.get());
        assertEquals(new Holder(5), holderLightFuture.get());
    }

    @Test
    void testSingleThreadUsage() throws LightExecutionException {
        pool = new ThreadPool(1);
        List<LightFuture<Integer>> simpleTasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            simpleTasks.add(pool.submit(() -> 5));
        }
        for (int shift = 0; shift < 10; shift++) {
            final int finalShift = shift;
            for (int start = 0; start < 10; start++) {
                List<LightFuture<Integer>> tasks = new ArrayList<>();
                final int finalStart = start;
                LightFuture<Integer> root = pool.submit(() -> finalStart);
                tasks.add(root);
                for (int i = 1; i < 10; i++) {
                    root = root.thenApply(x -> x + finalShift);
                    tasks.add(root);
                }
                while (true) {
                    int all = 1;
                    for (var task : tasks) {
                        if (!task.isReady()) {
                            all = 0;
                            break;
                        }
                    }
                    if (all == 1) {
                        break;
                    }
                }
                for (int i = 0; i < 10; i++) {
                    assertEquals(start + shift * i, (int) tasks.get(i).get());
                }
            }
        }
        while (true) {
            int all = 1;
            for (var task : simpleTasks) {
                if (!task.isReady()) {
                    all = 0;
                    break;
                }
            }
            if (all == 1) {
                break;
            }
        }
        for (var task : simpleTasks) {
            assertEquals(5, (int) task.get());
        }
    }

    private class Pair {
        private LightFuture<Integer> task;
        private Integer expectedResult;

        public LightFuture<Integer> getTask() {
            return task;
        }

        public Pair(LightFuture<Integer> task, Integer expectedResult) {
            this.task = task;
            this.expectedResult = expectedResult;
        }

        boolean check() throws LightExecutionException {
            return task.get().equals(expectedResult);
        }
    }

    @RepeatedTest(20)
    void testManyShortChains() throws LightExecutionException {
        List<Pair> pairs = new ArrayList<>();
        for (int shift = 0; shift < 10; shift++) {
            final int finalShift = shift;
            for (int start = 0; start < 10; start++) {
                List<LightFuture<Integer>> tasks = new ArrayList<>();
                final int finalStart = start;
                LightFuture<Integer> root = pool.submit(() -> finalStart);
                tasks.add(root);
                for (int i = 1; i < 10; i++) {
                    root = root.thenApply(x -> x + finalShift);
                    tasks.add(root);
                }
                for (int i = 0; i < 10; i++) {
                    pairs.add(new Pair(tasks.get(i), start + shift * i));
                }
            }
        }
        while (true) {
            int all = 1;
            for (var pair : pairs) {
                if (!pair.getTask().isReady()) {
                    all = 0;
                    break;
                }
            }
            if (all == 1) {
                break;
            }
        }
        for (var pair : pairs) {
            assertTrue(pair.check());
        }
    }

    @RepeatedTest(10)
    void testSubmitAfterShutdown() {
        pool.submit(() -> 5);
        pool.shutdown();
        assertThrows(Exception.class, () -> pool.submit(() -> 5));
    }
}