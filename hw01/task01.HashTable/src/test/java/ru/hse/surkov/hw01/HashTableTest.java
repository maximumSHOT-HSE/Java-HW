package ru.hse.surkov.hw01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable hashTable;

    @BeforeEach
    void createHashTable() {
        hashTable = new HashTable();
    }

    @Test
    void testZeroSizeJustAfterCreating() {
        assertEquals(0, hashTable.size());
    }

    @Test
    void testSizeWithGetPut() {
        for (int i = 1;i <= 20;i++) {
            assertEquals(i - 1, hashTable.size());

            hashTable.put(Integer.toString(i), "+");
            hashTable.get(Integer.toString(i));

            assertEquals(i, hashTable.size());
        }
    }

    @Test
    void testSizeFirstDoubleAddingThenRemoving() {
        for (int i = 1;i <= 20;i++) {
            hashTable.put(Integer.toString(i), "+");
        }
        for (int i = 1;i <= 20;i++) {
            hashTable.put(Integer.toString(i), "+2");
            assertEquals(20, hashTable.size());
        }
        for (int i = 20;i >= 1;i--) {
            hashTable.remove(Integer.toString(i));
            assertEquals(i - 1, hashTable.size());
        }
    }

    @Test
    void testSizeWithMixAddingAndRemoving() {
        for( int i = 1;i <= 20;i++) {
            hashTable.put(Integer.toString(i), "+");
            if ((i % 2) == 1) {
                hashTable.remove(Integer.toString(i));
            }
            assertEquals(i / 2, hashTable.size());
        }
    }

    @Test
    void testContainsWithEmptyHashTable() {
        for (int i = 1;i <= 100;i++) {
            assertFalse(hashTable.contains(Integer.toString(i)));
        }
    }

    @Test
    void testContainsWithFilledHashTable() {
        for (int i = 1;i <= 100;i++) {
            hashTable.put(Integer.toString(i), "+");
        }
        for (int i = 1;i <= 100;i++) {
            assertTrue(hashTable.contains(Integer.toString(i)));
        }
    }

    @Test
    void testContainsHashTableWithOnlyOddKeys() {
        for (int i = 1;i <= 100;i += 2) {
            hashTable.put(Integer.toString(i), "+");
        }
        for (int i = 1;i <= 100;i++) {
            assertEquals((i % 2) == 1, hashTable.contains(Integer.toString(i)));
        }
    }

    @Test
    void testGetWhileAdding() {
        for (int i = 1;i <= 100;i++) {
            hashTable.put(Integer.toString(i), Integer.toString(i * i));
            for (int j = 1;j <= 100;j++) {
                String foundValue = hashTable.get(Integer.toString(j));
                if (j <= i) {
                    assertEquals(Integer.toString(j * j), foundValue);
                } else {
                    assertNull(foundValue);
                }
            }
        }
    }

    @Test
    void testPutOnlyFreshKeys() {
        for (int i = 1;i <= 100;i++) {
            String previousValue = hashTable.put(Integer.toString(i), Integer.toString(i * i));
            assertNull(previousValue);
        }
    }

    @Test
    void testPutOverOldKeys() {
        for (int i = 1;i <= 100;i++) {
            hashTable.put(Integer.toString(i), Integer.toString(-i));
        }
        for (int i = 1;i <= 100;i++) {
            String previousValue = hashTable.put(Integer.toString(i), Integer.toString(i * i));
            assertEquals(Integer.toString(-i), previousValue);
        }
        for (int i = 1;i <= 100;i++) {
            String currentValue = hashTable.get(Integer.toString(i));
            assertEquals(Integer.toString(i * i), currentValue);
        }
    }

    @Test
    void testRemoveFromEmptyHashTable() {
        for (int i = 1;i <= 100;i++) {
            assertNull(hashTable.remove(Integer.toString(i)));
        }
    }

    @Test
    void testRemoveTwiceFromFilledHashTable() {
        for (int i = 1;i <= 100;i++) {
            hashTable.put(Integer.toString(i), Integer.toString(i * i));
        }
        for (int i = 1;i <= 100;i++) {
            String currentValue = hashTable.remove(Integer.toString(i));
            assertEquals(Integer.toString(i * i), currentValue);
            assertNull(hashTable.remove(Integer.toString(i)));
        }
    }

    @Test
    void testClearWithEmptyHashTable() {
        hashTable.clear();
        assertEquals(0, hashTable.size());
    }

    @Test
    void testClearWithFilledHashTable() {
        for (int i = 1;i <= 100;i++) {
            for (int j = 1;j <= i;j++) {
                hashTable.put(Integer.toString(i), "+");
            }
            hashTable.clear();
            assertEquals(0, hashTable.size());
        }
    }
}