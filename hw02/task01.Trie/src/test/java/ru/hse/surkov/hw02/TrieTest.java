package ru.hse.surkov.hw02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void createTrie() {
        trie = new Trie();
    }

    @Test
    void testAddNull() {
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
    }

    @Test
    void testAddEmptyString() {
        assertTrue(trie.add(""));
    }

    @Test
    void testAddSeveralEmptyStrings() {
        for (int i = 1;i <= 10; i++) {
            assertEquals(i == 1, trie.add(""));
        }
    }

    @Test
    void testAddSymbolsWithBigOrdCode() {
        char c = Character.MAX_VALUE;
        for (int i = 0; i < 100; i++, c--) {
            assertTrue(trie.add(Character.toString(c)));
        }
    }

    @Test
    void testAddSymbolsWithSmallOrdCode() {
        char c = Character.MIN_VALUE;
        for (int i = 0; i < 100; i++, c++) {
            assertTrue(trie.add(Character.toString(c)));
        }
    }

    /*
    * Non-prefix <=> there are not two strings,
    * such that one is the prefix of the other
    * */
    @Test
    void testAddNotNullDifferentNonPrefixStrings() {
        assertTrue(trie.add("a"));
        assertTrue(trie.add("ba"));
        assertTrue(trie.add("bcd"));
        assertTrue(trie.add("bce"));
        assertTrue(trie.add("abb"));
    }

    @Test
    void testAddRepeatedStrings() {
        for (int i = 1; i <= 100; i++) {
            assertTrue(trie.add(Integer.toString(i)));
            assertFalse(trie.add(Integer.toString(i)));
        }
        for (int i = 1; i <= 100; i++) {
            assertFalse(trie.add(Integer.toString(i)));
        }
    }

    @Test
    void testAddPrefixStrings() {
        assertTrue(trie.add("abcd"));
        assertTrue(trie.add("beeeeee"));
        assertTrue(trie.add("abc"));
        assertTrue(trie.add("abcde"));
        assertTrue(trie.add(""));
        assertFalse(trie.add("abcd"));
        assertTrue(trie.add("xxxxxxxx"));
        assertFalse(trie.add("abc"));
        assertFalse(trie.add(""));
    }

    @Test
    void testContainsNull() {
        assertFalse(trie.contains(null));
    }

    @Test
    void testContainsEmptyString() {
        assertFalse(trie.contains(""));
        assertTrue(trie.add(""));
        assertTrue(trie.contains(""));
    }

    @Test
    void testContainsSymbolsWithBigOrdCode() {
        char c = Character.MAX_VALUE;
        for (int i = 0; i < 100; i++, c--) {
            assertTrue(trie.add(Character.toString(c)));
        }
        c = Character.MAX_VALUE;
        for (int i = 0; i < 100; i++, c--) {
            assertTrue(trie.contains(Character.toString(c)));
        }
        c = Character.MIN_VALUE;
        for (int i = 0; i < 100; i++, c++) {
            assertFalse(trie.contains(Character.toString(c)));
        }
    }

    @Test
    void testContainsSymbolsWithSmallOrdCode() {
        char c = Character.MIN_VALUE;
        for (int i = 0; i < 100; i++, c++) {
            assertTrue(trie.add(Character.toString(c)));
        }
        c = Character.MIN_VALUE;
        for (int i = 0; i < 100; i++, c++) {
            assertTrue(trie.contains(Character.toString(c)));
        }
        c = Character.MAX_VALUE;
        for (int i = 0; i < 100; i++, c--) {
            assertFalse(trie.contains(Character.toString(c)));
        }
    }

    @Test
    void testContainsNotNullDifferentNonPrefixStrings() {
        String[] words = {"a", "ba", "bcd", "bce", "abb"};
        String[] notWords = {"1", "10", "123", "124", "2983"};
        for (var word : words) {
            assertTrue(trie.add(word));
        }
        for (var word : words) {
            assertTrue(trie.contains(word));
        }
        for (var notWord : notWords) {
            assertFalse(trie.contains(notWord));
        }
    }

    @Test
    void testContainsRepeatedStrings() {
        for (int i = 1; i <= 100; i++) {
            String si = Integer.toString(i);
            assertFalse(trie.contains(si));
            assertTrue(trie.add(si));
            assertTrue(trie.contains(si));
        }
        for (int i = 1; i <= 100; i++) {
            assertTrue(trie.contains(Integer.toString(i)));
        }
        for (int i = 101; i <= 200;i++) {
            assertFalse(trie.contains(Integer.toString(i)));
        }
    }

    @Test
    void testContainsPrefixStrings() {
        assertFalse(trie.contains(""));
        assertTrue(trie.add(""));
        assertTrue(trie.contains(""));
        for (char c = 'a'; c <= 'z'; c++) {
            var current = new StringBuilder(Character.toString(c));
            for (int i = 0; i < 100; i++, current.append(c)) {
                assertFalse(trie.contains(current.toString()));
                assertTrue(trie.add(current.toString()));
                assertTrue(trie.contains(current.toString()));
            }
        }
        assertFalse(trie.contains("abracadabra"));
    }

    @Test
    void testRemoveNull() {
        assertFalse(trie.remove(null));
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
        assertFalse(trie.remove(null));
    }

    @Test
    void testRemoveEmptyString() {
        assertFalse(trie.remove(""));
        assertTrue(trie.add(""));
        assertTrue(trie.contains(""));
        assertTrue(trie.remove(""));
        assertFalse(trie.contains(""));
        assertFalse(trie.remove(""));
    }

    @Test
    void testRemoveNotNullDifferentStrings() {
        for (int i = 0; i < 100; i++) {
            String si = Integer.toString(i);
            assertFalse(trie.remove(si));
            assertTrue(trie.add(si));
            assertTrue(trie.remove(si));
            assertFalse(trie.remove(si));
            assertFalse(trie.remove(si));
        }
    }

    @Test
    void testRemoveNotNullRepeatedStrings() {
        for (int i = -100; i <= 100; i++) {
            for (int iter = 0; iter < 10; iter++) {
                assertEquals(iter == 0, trie.add(Integer.toString(i)));
            }
            for (int iter = 0;iter < 10; iter++) {
                assertEquals(iter == 0, trie.remove(Integer.toString(i)));
            }
            if ((i % 2) == 0) {
                assertTrue(trie.add(Integer.toString(i)));
            }
        }
        for (int i = -200; i <= 200; i++) {
            assertEquals((-100 <= i && i <= 100 && (i % 2) == 0), trie.contains(Integer.toString(i)));
        }
    }

    @Test
    void testRemovePrefixStrings() {
        assertFalse(trie.contains(""));
        assertTrue(trie.add(""));
        assertTrue(trie.contains(""));
        for (char c = 'a'; c <= 'z'; c++) {
            var current = new StringBuilder(Character.toString(c));
            for (int i = 0; i < 100; i++, current.append(c)) {
                assertFalse(trie.contains(current.toString()));
                assertTrue(trie.add(current.toString()));
                assertTrue(trie.contains(current.toString()));
            }
            current = new StringBuilder(Character.toString(c));
            for (int i = 0; i < 100; i++, current.append(c)) {
                if ((i % 2) == 0) {
                    continue;
                }
                assertTrue(trie.remove(current.toString()));
                assertFalse(trie.remove(current.toString()));
                assertFalse(trie.remove(current.toString()));
                assertFalse(trie.remove(current.toString()));
            }
            current = new StringBuilder(Character.toString(c));
            for (int i = 0; i < 100; i++, current.append(c)) {
                assertEquals((i % 2) == 0, trie.contains(current.toString()));
            }
        }
        assertFalse(trie.contains("abracadabra"));
    }

    @Test
    void testSizeEmptyTrie() {
        assertEquals(0, trie.size());
    }

    @Test
    void testSizeAfterAddingNull() {
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
        assertEquals(0, trie.size());
    }

    @Test
    void testSizeAfterAddingSeveralEmptyStrings() {
        for (int i = 0; i < 100; i++) {
            assertEquals(i == 0, trie.add(""));
        }
        assertEquals(1, trie.size());
    }

    @Test
    void testSizeDifferentStrings() {
        for (int i = 1; i <= 100; i++) {
            assertTrue(trie.add(Integer.toString(i)));
            assertEquals(i, trie.size());
        }
        for (int i = 100; i >= 1; i--) {
            assertTrue(trie.remove(Integer.toString(i)));
            assertEquals(i - 1, trie.size());
        }
    }

    @Test
    void testSizeRepeatedStrings() {
        for (int i = 1; i <= 100; i++) {
            for (int iter = 0; iter < 10; iter++) {
                assertEquals(iter == 0, trie.add(Integer.toString(i)));
            }
            assertEquals(i, trie.size());
        }
    }

    @Test
    void testSizePrefixStrings() {
        var current = new StringBuilder();
        for (int i = 1; i <= 100; i++, current.append("a")) {
            assertEquals(i - 1, trie.size());
            assertTrue(trie.add(current.toString()));
            assertEquals(i, trie.size());
            assertTrue(trie.remove(current.toString()));
            assertEquals(i - 1, trie.size());
            assertTrue(trie.add(current.toString()));
            assertEquals(i, trie.size());
        }
    }

    @Test
    void testHowManyStartWithPrefixEmptyTrie() {
        for (int i = 0; i < 100; i++) {
            assertEquals(0, trie.howManyStartWithPrefix(Integer.toString(i)));
        }
    }

    @Test
    void testHowManyStartWithPrefixNull() {
        for (int i = 0; i < 100; i++) {
            assertEquals(0, trie.howManyStartWithPrefix(null));
            assertTrue(trie.add(Integer.toString(i)));
        }
        assertTrue(trie.add("abc"));
        assertThrows(IllegalArgumentException.class, () -> trie.add(null));
        assertEquals(0, trie.howManyStartWithPrefix(null));
        assertEquals(1, trie.howManyStartWithPrefix("a"));
    }

    @Test
    void testHowManyStartWithPrefixEmptyString() {
        for (int i = 0; i < 10; i++) {
            assertEquals(i == 0, trie.add(""));
        }
        assertEquals(1, trie.howManyStartWithPrefix(""));
    }

    @Test
    void testHowManyStartWithPrefixDifferentNotPrefixStrings() {
        String[] words = {
            "a", "ba", "xyz", "bcd", "bce", "bbb"
        };
        for (var word : words) {
            assertTrue(trie.add(word));
        }
        for (int iter = 0; iter < 10; iter++) {
            assertEquals(6, trie.howManyStartWithPrefix(""));
            assertEquals(1, trie.howManyStartWithPrefix("a"));
            assertEquals(4, trie.howManyStartWithPrefix("b"));
            assertEquals(1, trie.howManyStartWithPrefix("ba"));
            assertEquals(1, trie.howManyStartWithPrefix("bb"));
            assertEquals(1, trie.howManyStartWithPrefix("bbb"));
            assertEquals(0, trie.howManyStartWithPrefix("bbbbbbbbbbbb"));
            assertEquals(2, trie.howManyStartWithPrefix("bc"));
            assertEquals(1, trie.howManyStartWithPrefix("bce"));
            assertEquals(1, trie.howManyStartWithPrefix("bcd"));
            assertEquals(1, trie.howManyStartWithPrefix("x"));
            assertEquals(1, trie.howManyStartWithPrefix("xy"));
            assertEquals(1, trie.howManyStartWithPrefix("xyz"));
            assertEquals(0, trie.howManyStartWithPrefix("xxy"));
            assertEquals(0, trie.howManyStartWithPrefix(null));
        }
    }

    @Test
    void testHowManyStartWithPrefixWithPrefixStrings() {
        var current = new StringBuilder("a");
        for (int i = 1; i <= 10; i++, current.append('a')) {
            String s = current.toString();
            assertEquals(0, trie.howManyStartWithPrefix(s));
            assertTrue(trie.add(s));
            assertEquals(1, trie.howManyStartWithPrefix(s));
        }
        current = new StringBuilder("a");
        for (int i = 1; i <= 10; i++, current.append('a')) {
            assertEquals(11 - i, trie.howManyStartWithPrefix(current.toString()));
        }
    }

    @Test
    void testSerializing() {
        for(int i = 1; i <= 100; i++) {
            trie.add(Integer.toString(i));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            var current = new StringBuilder(Character.toString(c));
            for (int i = 1; i <= 10; i++, current.append(c)) {
                trie.add(current.toString());
            }
        }
        String[] words = {
            "abracadabra", "cacao", "kuku", "koko", "abc", "qwerty", "hse", "code", "jvm", "java", "javadoc"
        };
        for (var word : words) {
            trie.add(word);
        }
        var os = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> trie.serialize(os));
        Trie inputTrie = new Trie();
        assertDoesNotThrow(
            () -> inputTrie.deserialize(
                new ByteArrayInputStream(os.toByteArray())
            )
        );
        assertEquals(trie, inputTrie);
    }

    @Test
    void testSerializeEmptyTrie() throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeInt(0);
            objectOutputStream.writeBoolean(false);
        }
        var generatedOutputStream = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> trie.serialize(generatedOutputStream));
        assertArrayEquals(byteArrayOutputStream.toByteArray(), generatedOutputStream.toByteArray());
    }

    @Test
    void testSerializeWithNullArgument() {
        assertThrows(IllegalArgumentException.class, () -> trie.serialize(null));
    }

    @Test
    void testDeserializeEmptyTrie() throws IOException {
        var byteArrayOutputStream = new ByteArrayOutputStream();
        try (var objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeInt(0);
            objectOutputStream.writeBoolean(false);
        }
        Trie deserializedTrie = new Trie();
        assertDoesNotThrow(() -> deserializedTrie.deserialize(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())));
        assertEquals(trie, deserializedTrie);
    }

    @Test
    void testDeserializeWithNullArgument() {
        var helperTrie = new Trie();
        for (int i = 0; i < 100; i++) {
            trie.add(Integer.toString(i));
            helperTrie.add(Integer.toString(i));
        }
        assertEquals(100, trie.size());
        assertThrows(IllegalArgumentException.class, () -> trie.deserialize(null));
        assertEquals(helperTrie, trie);
    }

    @Test
    void testSerialize() throws IOException {
        trie.add("ac");
        trie.add("ad");
        trie.add("b");

        var expectedByteArrayOutputStream = new ByteArrayOutputStream();
        var foundByteArrayOutputStream = new ByteArrayOutputStream();

        try (var expectedObjectOutputStream = new ObjectOutputStream(expectedByteArrayOutputStream)) {
            expectedObjectOutputStream.writeInt(2);
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeChar('a');
            expectedObjectOutputStream.writeInt(2);
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeChar('c');
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeInt(1);
            expectedObjectOutputStream.writeChar('d');
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeInt(1);
            expectedObjectOutputStream.writeChar('b');
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeInt(1);
        }

        assertDoesNotThrow(() -> trie.serialize(foundByteArrayOutputStream));
        assertArrayEquals(expectedByteArrayOutputStream.toByteArray(), foundByteArrayOutputStream.toByteArray());
    }

    @Test
    void testDeserialize() throws IOException {
        var expectedByteArrayOutputStream = new ByteArrayOutputStream();

        try (var expectedObjectOutputStream = new ObjectOutputStream(expectedByteArrayOutputStream)) {
            expectedObjectOutputStream.writeInt(3);
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeChar('a');
            expectedObjectOutputStream.writeInt(2);
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeChar('x');
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeInt(1);
            expectedObjectOutputStream.writeChar('w');
            expectedObjectOutputStream.writeInt(1);
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeChar('w');
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeInt(1);
            expectedObjectOutputStream.writeChar('b');
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeInt(1);
            expectedObjectOutputStream.writeChar('c');
            expectedObjectOutputStream.writeBoolean(false);
            expectedObjectOutputStream.writeInt(1);
        }

        var helperTrie = new Trie();

        helperTrie.add("ax");
        helperTrie.add("aww");
        helperTrie.add("b");
        helperTrie.add("c");

        assertDoesNotThrow(() -> trie.deserialize(new ByteArrayInputStream(expectedByteArrayOutputStream.toByteArray())));
        assertEquals(helperTrie, trie);
    }
}