package ru.hse.surkov.hw01;

import java.util.Arrays;

public class HashTable {

    private List[] table;
    private int capacity; // physical size of table
    private int size; // actual number of keys
    private static final int START_CAPACITY = 10;

    // fill factor = NUM / DENOM
    private static final int NUM = 1; // numerator
    private static final int DENOM = 2; // denominator

    private void initEmpty(int capacity) {
        this.capacity = capacity;
        size = 0;
        table = new List[capacity];
        Arrays.fill(table, new List());
    }

    public HashTable() {
        initEmpty(START_CAPACITY);
    }

    private HashTable(int capacity) {
        initEmpty(capacity);
    }

    private void copy(HashTable original) throws IllegalArgumentException {
        if (original == null) {
            throw new IllegalArgumentException("original can not be null");
        }
        table = original.table;
        capacity = original.capacity;
        size = original.size;
    }

    /*
     * Doubles the capacity of HashTable
     * */
    private void rebuild() {
        HashTable doubleHashTable = new HashTable(capacity * 2);
        for (List chain : table) {
            List.ListVertex[] records = chain.getRecords();
            for (List.ListVertex vertex : records) {
                doubleHashTable.put(vertex.getKey(), vertex.getValue());
            }
        }
        copy(doubleHashTable);
    }

    private int getPosition(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        int position = key.hashCode() % capacity;
        if (position < 0) {
            position += capacity;
        }
        return position;
    }

    public int size() {
        return size;
    }

    /**
     * {@link ru.hse.surkov.hw01.List#contains(String)}
     * */
    public boolean contains(String key) {
        if (key == null) {
            return false;
        }
        int position = getPosition(key);
        return table[position].contains(key);
    }

    /**
     * {@link ru.hse.surkov.hw01.List#get(String)}
     * */
    public String get(String key) {
        if (key == null) {
            return null;
        }
        int position = getPosition(key);
        return table[position].get(key);
    }

    /**
     * {@link ru.hse.surkov.hw01.List#put(String, String)}
     * */
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        int position = getPosition(key);
        String previousValue = table[position].put(key, value);
        if (previousValue == null) {
            size++;
        }
        if (size * (long)DENOM > capacity * (long)NUM) { // size > capacity * (NUM / DENOM). Calculations without loss of accuracy
            rebuild();
        }
        return previousValue;
    }

    /**
     * {@link ru.hse.surkov.hw01.List#remove(String)}
     * */
    public String remove(String key) {
        if (key == null) {
            return null;
        }
        int position = getPosition(key);
        String value = table[position].remove(key);
        if (value != null) {
            size--;
        }
        return value;
    }

    public void clear() {
        initEmpty(START_CAPACITY);
    }
}
