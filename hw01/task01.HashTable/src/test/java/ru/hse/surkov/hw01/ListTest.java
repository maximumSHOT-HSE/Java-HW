package ru.hse.surkov.hw01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ListTest {

    private List list;

    @BeforeEach
    void createList() {
        list = new List();
    }

    @Test
    void testRemoveWithNull() {
        assertNull(list.remove(null));
        for (int i = 1; i <= 100; i++) {
            list.put(Integer.toString(i), "!");
            assertNull(list.remove(null));;
        }
        for (int i = 1; i <= 100; i++) {
            list.remove(Integer.toString(i));
            assertNull(list.remove(null));
        }
    }

    @Test
    void testContains() {
        for (int i = 1; i <= 100; i++) {
            list.put(Integer.toString(i), "!");
        }
        for (int i = 1; i <= 200; i++) {
            assertEquals(i <= 100, list.contains(Integer.toString(i)));
        }
    }

    @Test
    void testContainsWithNullArgument() {
        for (int i = 1; i <= 100; i++) {
            list.put(Integer.toString(i), "abc");
        }
        assertThrows(IllegalArgumentException.class, () -> list.put(null, "!"));
        assertFalse(list.contains(null));
    }

    @Test
    void testGet() {
        for(int i = 1; i <= 100; i++) {
            list.put(Integer.toString(i), Integer.toString(i * 5 + 3));
        }
        for(int i = 1; i <= 200; i++) {
            if (i <= 100) {
                assertEquals(Integer.toString(i * 5 + 3), list.get(Integer.toString(i)));
            } else {
                assertNull(list.get(Integer.toString(i)));
            }
        }
    }

    @Test
    void testClear() {
        for (int i = 1; i <= 100; i++) {
            list.put(Integer.toString(i), "!");
        }
        list.clear();
        assertEquals(0, list.size());
    }

    @Test
    void testPutWithNull() {
        assertThrows(IllegalArgumentException.class, () -> list.put(null, null));
        assertThrows(IllegalArgumentException.class, () -> list.put(null, "!"));
        assertThrows(IllegalArgumentException.class, () -> list.put("!", null));
    }

    @Test
    void testGetWithNull() {
        assertThrows(IllegalArgumentException.class, () -> list.put(null, null));
        assertThrows(IllegalArgumentException.class, () -> list.put(null, "!"));
        assertThrows(IllegalArgumentException.class, () -> list.put("!", null));
        assertNull(list.get("!"));
        assertNull(list.put("a", "b"));
        assertEquals("b", list.get("a"));
        assertNull(list.get(null));
    }
}