package ru.hse.surkov.hw06;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class QSorterTest {

    private static final int SEED = 153;
    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random(SEED);
    }

    private <T> boolean isSorted(T[] array, Comparator<? super T> comparator) {
        return IntStream
                .range(0, array.length - 1)
                .allMatch(i ->
                    comparator.compare(array[i], array[i + 1]) <= 0
                );
    }

    @Test
    void testIsSortedSmallArrayIntegers() {
        Integer[] a = random.ints().limit(100).boxed().toArray(Integer[]::new);
        QSorter.quickSort(a, 10, 4);
        assertTrue(isSorted(a, Comparator.naturalOrder()));
    }

    @Test
    void testIsSortedBigArrayIntegers() {
        Integer[] a = random.ints().limit(1000000).boxed().toArray(Integer[]::new);
        QSorter.quickSort(a, 10, 4);
        assertTrue(isSorted(a, Comparator.naturalOrder()));
    }

    @Test
    void testIsSortedSmallArrayWithComparator() {
        Integer[] a = random.ints().limit(100).boxed().toArray(Integer[]::new);
        Comparator<Integer> comparator = (o1, o2) -> o2 - o1;
        QSorter.quickSort(a, 10, 4, comparator);
        assertTrue(isSorted(a, comparator));
    }

    @Test
    void testIsSortedBigArrayWithComparator() {
        Integer[] a = random.ints().limit(1000000).boxed().toArray(Integer[]::new);
        Comparator<Integer> comparator = (o1, o2) -> o2 - o1;
        QSorter.quickSort(a, 10, 4, comparator);
        assertTrue(isSorted(a, comparator));
    }

    class A implements Comparable<A> {
        private int value;

        public A(int value) {
            this.value = value;
        }

        public int getSum() {
            int x = value;
            if (x < 0) {
                x = -x;
            }
            int sum = 0;
            while (x > 0) {
                sum += x % 10;
                x /= 10;
            }
            return sum;
        }

        @Override
        public int compareTo(@NotNull QSorterTest.A o) {
            return o.getSum() - getSum();
        }
    }

    @Test
    void testIsSortedMyClassSmallArray() {
        A[] a = random.ints().limit(100).mapToObj(A::new).toArray(A[]::new);
        QSorter.quickSort(a, 10, 2);
        assertTrue(isSorted(a, Comparator.naturalOrder()));
    }

    @Test
    void testIsSortedMyClassBigArray() {
        A[] a = random.ints().limit(1000000).mapToObj(A::new).toArray(A[]::new);
        QSorter.quickSort(a, 30, 2);
        assertTrue(isSorted(a, Comparator.naturalOrder()));
    }

    @Test
    void testIsSortedMyClassSmallArrayWithComparator() {
        A[] a = random.ints().limit(100).mapToObj(A::new).toArray(A[]::new);
        Comparator<A> comparator = Comparator.comparingInt(A::getSum);
        QSorter.quickSort(a, 10, 2, comparator);
        assertTrue(isSorted(a, comparator));
    }

    @Test
    void testIsSortedMyClassBigArrayWithComparator() {
        A[] a = random.ints().limit(1000000).mapToObj(A::new).toArray(A[]::new);
        Comparator<A> comparator = Comparator.comparingInt(A::getSum);
        QSorter.quickSort(a, 30, 4, comparator);
        assertTrue(isSorted(a, comparator));
    }
}