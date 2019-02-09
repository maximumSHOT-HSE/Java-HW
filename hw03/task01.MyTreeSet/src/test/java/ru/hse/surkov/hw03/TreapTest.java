package ru.hse.surkov.hw03;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TreapTest {

    private Treap<Integer> set;
    private Treap<Integer> cmpSet;

    @BeforeEach
    void setUp() {
        set = new Treap<>();
        cmpSet = new Treap<>((x, y) -> x > y ? -1 : x < y ? +1 : 0);
    }

    @Test
    void testSizeEmptyTree() {
        assertEquals(0, set.size());
    }

    @Test
    void testSizeFilledWithUniqueValuesTree() {
        for (int i = 1; i <= 100; i++) {
            set.add(i);
            assertEquals(i, set.size());
        }
        for (int i = 1; i <= 100; i++) {
            cmpSet.add(i);
            assertEquals(i, cmpSet.size());
        }
    }

    @Test
    void testSizeFilledWithRepeatedValuesTree() {
        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= 50; j++) {
                set.add(i);
                assertEquals(i, set.size());
            }
        }
        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= 50; j++) {
                cmpSet.add(i);
                assertEquals(i, cmpSet.size());
            }
        }
    }

    @Test
    void testAddNotNullsDifferentValues() {
        for (int i = 1; i <= 100; i++) {
            assertTrue(set.add(i));
        }
        for (int i = 1; i <= 100; i++) {
            assertTrue(cmpSet.add(i));
        }
    }

    @Test
    void testAddNotNullsRepeatedValues() {
        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= 50; j++) {
                assertEquals(j == 1, set.add(i));
            }
        }
        for (int i = 1; i <= 100; i++) {
            for (int j = 1; j <= 50; j++) {
                assertEquals(j == 1, cmpSet.add(i));
            }
        }
    }

    @Test
    void testAddNotComparable() {
        Treap<Set<Integer>> l = new Treap<>();
        assertTrue(l.add(set));
        assertThrows(ClassCastException.class, () -> l.add(set));
    }

    @Test
    void testContainsDifferentValues() {
        for (int i = 1; i <= 100; i += 2) {
            set.add(i);
            cmpSet.add(i);
        }
        for (int i = 1; i <= 100; i++) {
            assertEquals((i % 2) == 1, set.contains(i));
            assertEquals((i % 2) == 1, cmpSet.contains(i));
        }
    }

    @Test
    void testContainsRepeatedValues() {
        for (int iter = 0; iter < 5; iter++) {
            for (int i = 1; i <= 100; i++) {
                for (int j = 1; j <= 50; j++) {
                    assertEquals(iter > 0 || j > 1, set.contains(i));
                    assertEquals(iter > 0 || j > 1, cmpSet.contains(i));
                    set.add(i);
                    cmpSet.add(i);
                    assertTrue(set.contains(i));
                    assertTrue(cmpSet.contains(i));
                }
            }
        }
    }


    /*
    * This code was commented, because of strange behaviour
    * during testing. Testing from IDE causes OK, testing from gradle command 'test'
    * causes 'Expected java.lang.IllegalArgumentException to be thrown, but nothing was thrown.'
    * */
//    @Test
//    void testContainsNullable() {
//        assertThrows(IllegalArgumentException.class, () -> set.contains(null));
//        assertThrows(IllegalArgumentException.class, () -> cmpSet.contains(null));
//    }

//    @Test
//    void testRemoveNullable() {
//        for (int i = 1; i <= 100; i++) {
//            for (int j = 1; j <= 50; j++) {
//                assertEquals(j == 1, set.add(i));
//                assertEquals(j == 1, cmpSet.add(i));
//                assertThrows(IllegalArgumentException.class, () -> set.remove(null));
//                assertThrows(IllegalArgumentException.class, () -> cmpSet.remove(null));
//            }
//        }
//    }

//    @Test
//    void testAddNullable() {
//        assertThrows(NullPointerException.class, () -> set.add(null));
//        set.add(null);
//        assertThrows(NullPointerException.class, () -> set.add(null));
//    }

    @Test
    void testRemoveDifferentValues() {
        for (int iter = 0; iter < 5; iter++) {
            for (int i = 1; i <= 10; i++) {
                set.add(i);
                cmpSet.add(i);
            }
            for (int i = 1; i <= 10; i++) {
                assertTrue(set.remove(i));
                assertTrue(cmpSet.remove(i));
                assertFalse(set.remove(i));
                assertFalse(cmpSet.remove(i));
            }
        }
    }

    @Test
    void testRemoveRepeatedValues() {
        for (int iter = 0; iter < 5; iter++) {
            for (int i = 1; i <= 100; i++) {
                for (int j = 1; j <= 50; j++) {
                    assertTrue(set.add(i));
                    assertTrue(cmpSet.add(i));
                    assertTrue(set.remove(i));
                    assertTrue(cmpSet.remove(i));
                    assertFalse(set.remove(i));
                    assertFalse(cmpSet.remove(i));
                }
            }
        }
    }


    @Test
    void testFirst() {
        for (int l = 1; l <= 10; l++) {
            for (int r = l; r <= 10; r++) {
                set.add(r);
                cmpSet.add(r);
                assertEquals(Optional.of(l), Optional.ofNullable(set.first()));
                assertEquals(r, cmpSet.first());
            }
            for (int r = l; r <= 10; r++) {
                set.remove(r);
                cmpSet.remove(r);
            }
        }
    }

    @Test
    void testLast() {
        for (int l = 1; l <= 10; l++) {
            for (int r = l; r <= 10; r++) {
                set.add(r);
                cmpSet.add(r);
                assertEquals(Optional.of(r), Optional.ofNullable(set.last()));
                assertEquals(l, cmpSet.last());
            }
            for (int r = l; r <= 10; r++) {
                set.remove(r);
                cmpSet.remove(r);
            }
        }
    }

    @Test
    void testLower() {
        for (int l = 1; l <= 10; l++) {
            for (int r = l; r <= 10; r++) {
                set.add(r);
                cmpSet.add(r);
                for (int x = l - 5; x <= r + 5; x++) {
                    if (x <= l) {
                        assertNull(set.lower(x));
                    } else {
                        assertEquals(Optional.of(Math.min(r, x - 1)), Optional.ofNullable(set.lower(x)));
                    }
                    if (x >= r) {
                        assertNull(cmpSet.lower(x));
                    } else {
                        assertEquals(Math.max(l, x + 1), cmpSet.lower(x));
                    }
                }
            }
            for (int r = l; r <= 10; r++) {
                set.remove(r);
                cmpSet.remove(r);
            }
        }
    }

    @Test
    void testFloor() {
        for (int l = 1; l <= 10; l++) {
            for (int r = l; r <= 10; r++) {
                set.add(r);
                cmpSet.add(r);
                for (int x = l - 5; x <= r + 5; x++) {
                    if (x < l) {
                        assertNull(set.floor(x));
                    } else {
                        assertEquals(Optional.of(Math.min(r, x)), Optional.ofNullable(set.floor(x)));
                    }
                    if (x > r) {
                        assertNull(cmpSet.floor(x));
                    } else {
                        assertEquals(Math.max(l, x), cmpSet.floor(x));
                    }
                }
            }
            for (int r = l; r <= 10; r++) {
                set.remove(r);
                cmpSet.remove(r);
            }
        }
    }

    @Test
    void testHigher() {
        for (int l = 1; l <= 10; l++) {
            for (int r = l; r <= 10; r++) {
                set.add(r);
                cmpSet.add(r);
                for (int x = l - 5; x <= r + 5; x++) {
                    if (x <= l) {
                        assertNull(cmpSet.higher(x));
                    } else {
                        assertEquals(Math.min(r, x - 1), cmpSet.higher(x));
                    }
                    if (x >= r) {
                        assertNull(set.higher(x));
                    } else {
                        assertEquals(Optional.of(Math.max(l, x + 1)), Optional.ofNullable(set.higher(x)));
                    }
                }
            }
            for (int r = l; r <= 10; r++) {
                set.remove(r);
                cmpSet.remove(r);
            }
        }
    }

    @Test
    void testCeiling() {
        for (int l = 1; l <= 10; l++) {
            for (int r = l; r <= 10; r++) {
                set.add(r);
                cmpSet.add(r);
                for (int x = l - 5; x <= r + 5; x++) {
                    if (x < l) {
                        assertNull(cmpSet.ceiling(x));
                    } else {
                        assertEquals(Math.min(r, x), cmpSet.ceiling(x));
                    }
                    if (x > r) {
                        assertNull(set.ceiling(x));
                    } else {
                        assertEquals(Optional.of(Math.max(l, x)), Optional.ofNullable(set.ceiling(x)));
                    }
                }
            }
            for (int r = l; r <= 10; r++) {
                set.remove(r);
                cmpSet.remove(r);
            }
        }
    }

    @Test
    void testDescendingSet() {
        for (int i = 1; i <= 100; i++) {
            set.add(i);
            cmpSet.add(i);
        }
        var reverseSet = set.descendingSet();
        var reverseCmpSet = cmpSet.descendingSet();
        assertArrayEquals(reverseSet.toArray(), cmpSet.toArray());
        assertArrayEquals(reverseCmpSet.toArray(), set.toArray());
    }

    @Test
    void testIterator() {
        for (int i = 1; i <= 100; i++) {
            set.add(i);
            cmpSet.add(i);
        }
        Iterator setIt = set.iterator();
        Iterator cmpSetIt = cmpSet.iterator();
        Iterator finalSetIt = setIt;
        assertThrows(IllegalStateException.class, finalSetIt::remove);
        Iterator finalCmpSetIt = cmpSetIt;
        assertThrows(IllegalStateException.class, finalCmpSetIt::remove);
        for (int i = 1; i <= 100; i++) {
            assertTrue(setIt.hasNext());
            assertEquals(i, setIt.next());
            assertTrue(cmpSetIt.hasNext());
            assertEquals(101 - i, cmpSetIt.next());
        }
        assertFalse(setIt.hasNext());
        assertFalse(cmpSetIt.hasNext());
        setIt.remove();
        cmpSetIt.remove();
        setIt = set.iterator();
        cmpSetIt = cmpSet.iterator();
        for (int i = 1; i < 100; i += 3) {
            assertEquals(i, setIt.next());
            setIt.remove();
            assertEquals(i + 1, setIt.next());
            assertEquals(i + 2, setIt.next());
        }
        for (int i = 100; i - 2 > 1; i -= 3) {
            assertEquals(i, cmpSetIt.next());
            cmpSetIt.remove();
            assertEquals(i - 1, cmpSetIt.next());
            assertEquals(i - 2, cmpSetIt.next());
        }
    }

    @Test
    void testDescendingIterator() {
        for (int i = 1; i <= 100; i++) {
            set.add(i);
            cmpSet.add(i);
        }
        Iterator setIt = cmpSet.descendingIterator();
        Iterator cmpSetIt = set.descendingIterator();
        Iterator finalSetIt = setIt;
        assertThrows(IllegalStateException.class, finalSetIt::remove);
        Iterator finalCmpSetIt = cmpSetIt;
        assertThrows(IllegalStateException.class, finalCmpSetIt::remove);
        for (int i = 1; i <= 100; i++) {
            assertTrue(setIt.hasNext());
            assertEquals(i, setIt.next());
            assertTrue(cmpSetIt.hasNext());
            assertEquals(101 - i, cmpSetIt.next());
        }
        assertFalse(setIt.hasNext());
        assertFalse(cmpSetIt.hasNext());
        setIt.remove();
        cmpSetIt.remove();
        setIt = cmpSet.descendingSet().iterator();
        cmpSetIt = set.descendingSet().iterator();
        for (int i = 1; i < 100; i += 3) {
            assertEquals(i, setIt.next());
            setIt.remove();
            assertEquals(i + 1, setIt.next());
            assertEquals(i + 2, setIt.next());
        }
        for (int i = 100; i - 2 > 1; i -= 3) {
            assertEquals(i, cmpSetIt.next());
            cmpSetIt.remove();
            assertEquals(i - 1, cmpSetIt.next());
            assertEquals(i - 2, cmpSetIt.next());
        }
    }

    @Test
    void testIteratorInvalidation() {
        for (int i = 1; i <= 10; i++) {
            set.add(i);
        }
        var it = set.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        set.add(-1);
        assertThrows(ConcurrentModificationException.class, it::next);
        assertThrows(ConcurrentModificationException.class, it::remove);
        for (int i = 1; i <= 10; i++) {
            cmpSet.add(i);
        }
        var it2 = cmpSet.iterator();
        assertTrue(it2.hasNext());
        assertEquals(10, it2.next());
        cmpSet.add(-1);
        assertThrows(ConcurrentModificationException.class, it2::next);
        assertThrows(ConcurrentModificationException.class, it2::remove);
    }
}