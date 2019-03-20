package ru.hse.surkov.hw06;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Class with method of sorting
 * array using concurrent quick sort
 * */
public class QSorter {

    private static final int ARRAY_SIZE_STEP = 100_000;
    private static final int MAXIMUM_ARRAY_SIZE = 1_000_000;
    private static final int BENCHMARKING_THREADS_NUMBER = 8;
    private static final int SEED = 42;
    private volatile static Random random = new Random(SEED);
    private static final int BENCHMARKING_SEED = 153;
    private static Random benchmarking_random = new Random(BENCHMARKING_SEED);

    /*
    * Generates random value in range between left and right inclusively
    * */
    private static int randInSegment(int left, int right) {
        if (left > right) {
            throw new IllegalArgumentException(
                    "left can not be greater than right, but found that "
                            + left + " > " + right);
        }
        int segmentLength = right - left + 1;
        return random.nextInt(segmentLength) + left;
    }

    /*
    * Swap two elements in array by given indexes
    * */
    private static <T> void swap(T[] array, int i, int j) {
        T helper = array[i];
        array[i] = array[j];
        array[j] = helper;
    }

    /**
     * Sorts given array using the concurrent
     * quick sort algorithm.
     * @param array to be sorted
     * @param minElementsNumber indicates minimum number of elements to
     * use concurrency, hence if some quick sort branch will get a segment
     * of the array with length less than minElementsNumber then a non-parallel
     * sorting algorithm will be used.
     * @param threadsNumber indicates how many threads should be used during sorting.
     * @param comparator is necessary for specifying order of sorting
     * */
    public static <T> void quickSort(
            T[] array, int minElementsNumber, int threadsNumber,
            Comparator<? super T> comparator) {
        if (array.length == 0) {
            return;
        }
        ExecutorService pool = Executors.newFixedThreadPool(threadsNumber);
        CountDownLatch latch = new CountDownLatch(array.length);
        pool.submit(new Task<>(array, 0, array.length - 1,
                pool, latch, minElementsNumber, comparator));
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }
    }

    /**
     * Method the same as the {@link QSorter#quickSort(Object[], int, int, Comparator)},
     * but instead of comparator this methods requires from type T to be comparable.
     * */
    public static <T extends Comparable<? super T>> void quickSort(
            T[] array, int minElementsNumber, int threadsNumber) {
        quickSort(array, minElementsNumber, threadsNumber, Comparator.naturalOrder());
    }

    private static long measureExecutionTime(Runnable task) {
        long startTime = System.currentTimeMillis();
        task.run();
        long finishTime = System.currentTimeMillis();
        return finishTime - startTime;
    }

    /**
     * Method finds an array size wherein a concurrent version of sorting
     * algorithm for the Integers will win by the time
     * against a non-concurrent version.
     * */
    public static <T> void compareSorts() {
        for (int size = ARRAY_SIZE_STEP;
             size <= MAXIMUM_ARRAY_SIZE; size += ARRAY_SIZE_STEP) {
            Integer[] a = benchmarking_random
                    .ints()
                    .limit(size)
                    .boxed()
                    .toArray(Integer[]::new);
            Integer[] b = Arrays.copyOf(a, a.length);
            long nonConcurrentExecutionTime = measureExecutionTime(
                () -> Arrays.sort(a)
            );
            long concurrentExecutionTime = measureExecutionTime(
                () -> quickSort(b, 30, BENCHMARKING_THREADS_NUMBER)
            );
            System.out.println("----------------");
            System.out.println("Array size: " + size);
            System.out.println("Concurrent quick sort execution time: "
                    + concurrentExecutionTime + " ms");
            System.out.println("Non-concurrent quick sort execution time: "
                    + nonConcurrentExecutionTime + " ms");
            System.out.println("----------------");
            if (concurrentExecutionTime < nonConcurrentExecutionTime) {
                System.out.println("Concurrent wins!");
            } else {
                System.out.println("Non-concurrent wins!");
            }
            System.out.println("----------------");
        }
    }

    /*
    * Task of sorting subarray from left to right inclusively
    * */
    private static class Task <T> implements Runnable {

        /*
        * Minimum number of elements for using quick sort algorithm, hence
        * if subarray length less than this constant then
        * insertion sorting algorithm will be used.
        * */
        private static final int QUICK_SORT_MIN_ELEMENTS = 30;

        Lock lock = new ReentrantLock();
        private T[] array;
        private int left;
        private int right;
        private ExecutorService pool;
        private CountDownLatch latch;
        private int minElementsNumber;
        private Comparator<? super T> comparator;

        public Task(
                T[] array, int left, int right,
                ExecutorService pool, CountDownLatch latch, int minElementsNumber,
                Comparator<? super  T> comparator) {
            this.array = array;
            this.left = left;
            this.right = right;
            this.pool = pool;
            this.latch = latch;
            this.minElementsNumber = minElementsNumber;
            this.comparator = comparator;
        }

        private void insertionSort() {
            for (int i = left + 1; i <= right; i++) {
                for (int j = i; j > left &&
                        comparator.compare(array[j], array[j - 1]) < 0; j--) {
                    swap(array, j, j - 1);
                }
            }
        }

        // Decrease latch by max(0, value)
        private void decreaseLatch(int value) {
            if (value < 0) {
                value = 0;
            }
            lock.lock();
            try {
                for (int i = 0; i < value; i++) {
                    latch.countDown();
                }
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void run() {
            if (left > right) {
                return;
            }
            if (left == right) {
                decreaseLatch(1);
                return;
            }
            int segmentLength = right - left + 1;
            T separator = array[randInSegment(left, right)];
            partition(separator);
            int leftPointer = getLowerPointer(separator);
            int rightPointer = getUpperPointer(separator);
            Task<T> leftTask = new Task<>(array, left, leftPointer,
                    pool, latch, minElementsNumber, comparator);
            Task<T> rightTask = new Task<>(array, rightPointer, right,
                    pool, latch, minElementsNumber, comparator);
            if (segmentLength < minElementsNumber &&
                    segmentLength < QUICK_SORT_MIN_ELEMENTS) {
                insertionSort();
                decreaseLatch(segmentLength);
            } else {
                decreaseLatch(rightPointer - leftPointer - 1);
                if (segmentLength < minElementsNumber) { // non-concurrent version
                    leftTask.run();
                    rightTask.run();
                } else { // concurrent version
                    pool.submit(leftTask);
                    pool.submit(rightTask);
                }
            }
        }

        private int getLowerPointer(T separator) {
            int pointer = left;
            while (comparator.compare(array[pointer], separator) != 0) {
                pointer++;
            }
            return pointer - 1;
        }

        private int getUpperPointer(T separator) {
            int pointer = right;
            while (comparator.compare(array[pointer], separator) != 0) {
                pointer--;
            }
            return pointer + 1;
        }

        private void partition(T separator) {
            int position = left;
            for (int i = left; i <= right; i++) {
                if (comparator.compare(array[i], separator) < 0) {
                    swap(array, i, position);
                    position++;
                }
            }
            for (int i = position; i <= right; i++) {
                if (comparator.compare(array[i], separator) == 0) {
                    swap(array, i, position);
                    position++;
                }
            }
        }
    }
}
