package ru.hse.surkov.hw01;

import java.util.Arrays;

public class HashTable {

    private List[] table;
    private int capacity; // physical size of table
    private int size; // actual number of keys
    private static final int startCapacity = 10;

    // fill factor = NUM / DENOM
    private static final int NUM = 1; // numerator
    private static final int DENOM = 2; // denominator

    public HashTable() {
        this(startCapacity);
    }

    private HashTable(int capacity) {
        this.capacity = capacity;
        size = 0;
        table = new List[capacity];
        Arrays.fill(table, new List());
    }

    private void copy(HashTable original) {
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

    private int getPosition(String key) {
        int position = key.hashCode() % capacity;
        if (position < 0) {
            position += capacity;
        }
        return position;
    }

    public int size() {
        return size;
    }

    public boolean contains(String key) {
        int position = getPosition(key);
        return table[position].contains(key);
    }

    /**
     * {@link ru.hse.surkov.hw01.List#get(String)}
     * */
    public String get(String key) {
        int position = getPosition(key);
        return table[position].get(key);
    }

    /**
     * {@link ru.hse.surkov.hw01.List#put(String, String)}
     * */
    public String put(String key, String value) {
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
        int position = getPosition(key);
        String value = table[position].remove(key);
        if (value != null) {
            size--;
        }
        return value;
    }

    public void clear() {
        size = 0;
        Arrays.fill(table, new List());
    }
}
