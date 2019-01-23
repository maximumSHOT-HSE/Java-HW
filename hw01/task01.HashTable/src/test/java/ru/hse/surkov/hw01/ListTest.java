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
    void testGetRecordsWithEmptyList() {
        List.ListVertex[] records = list.getRecords();
        assertEquals(0, records.length);
    }

    @Test
    void testGetRecodsWithPut() {
        for (int iter = 1;iter <= 10;iter++) {
            for (int i = 1; i <= 20; i++) {
                list.put(Integer.toString(i), Integer.toString(i * 10));
            }
        }
        List.ListVertex[] records = list.getRecords();
        assertEquals(20, records.length);
        for (int i = 1;i <= 20;i++) {
            assertEquals(Integer.toString(i), records[i - 1].getKey());
            assertEquals(Integer.toString(i * 10), records[i - 1].getValue());
        }
    }

    @Test
    void testGetRecordsWithFirstPutThenRemoveOperations() {
        for (int i = 1;i <= 20;i++) {
            list.put(Integer.toString(i), "+");
        }
        for (int i = 1;i <= 20;i++) {
            list.remove(Integer.toString(i));
        }
        List.ListVertex[] records = list.getRecords();
        assertEquals(0, records.length);
    }

    @Test
    void testGetRecordsAfterEachDoublePuttingAndRemoving() {
        for (int i = 1;i <= 20;i++) {
            list.put(Integer.toString(i * 2), "a");
            list.put(Integer.toString(i * 2 + 1), "b");
            list.remove(Integer.toString(i * 2));
            List.ListVertex[] records = list.getRecords();
            assertEquals(i, records.length);
            for (int j = 1;j <= i;j++) {
                assertEquals(Integer.toString(2 * j + 1), records[j - 1].getKey());
                assertEquals("b", records[j - 1].getValue());
            }
        }
    }

    @Test
    void testRemoveWithNull() {
        assertNull(list.remove(null));
        for (int i = 1;i <= 100;i++) {
            list.put(Integer.toString(i), "!");
            assertNull(list.remove(null));;
        }
        for (int i = 1;i <= 100;i++) {
            list.remove(Integer.toString(i));
            assertNull(list.remove(null));
        }
    }

    @Test
    void testContains() {
        for (int i = 1;i <= 100;i++) {
            list.put(Integer.toString(i), "!");
        }
        for (int i = 1;i <= 200;i++) {
            assertEquals(i <= 100, list.contains(Integer.toString(i)));
        }
    }

    @Test
    void testContainsWithNullArgument() {
        for (int i = 1;i <= 100;i++) {
            list.put(Integer.toString(i), "abc");
        }
        assertThrows(IllegalArgumentException.class, () -> list.put(null, "!"));
        assertFalse(list.contains(null));
    }

    @Test
    void testGet() {
        for(int i = 1;i <= 100;i++) {
            list.put(Integer.toString(i), Integer.toString(i * 5 + 3));
        }
        for(int i = 1;i <= 200;i++) {
            if (i <= 100) {
                assertEquals(Integer.toString(i * 5 + 3), list.get(Integer.toString(i)));
            } else {
                assertNull(list.get(Integer.toString(i)));
            }
        }
    }

    @Test
    void testClear() {
        for (int i = 1;i <= 100;i++) {
            list.put(Integer.toString(i), "!");
        }
        list.clear();
        List.ListVertex[] records = list.getRecords();
        assertEquals(0, records.length);
    }

    @Test
    void testGetPutWithNull() {
        assertThrows(IllegalArgumentException.class, () -> list.put(null, null));
        assertThrows(IllegalArgumentException.class, () -> list.put(null, "!"));
        assertThrows(IllegalArgumentException.class, () -> list.put("!", null));
        assertNull(list.get("!"));
        assertNull(list.put("a", "b"));
        assertEquals("b", list.get("a"));
        assertNull(list.get(null));
    }
}