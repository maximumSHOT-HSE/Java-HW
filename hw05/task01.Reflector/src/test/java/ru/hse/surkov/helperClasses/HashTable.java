package ru.hse.surkov.helperClasses;

import java.util.Arrays;

/** HashTable with chains method */
public class HashTable {

    private MyList[] table;
    private int capacity; // physical size of table
    private int size; // actual number of keys
    private static final int START_CAPACITY = 10;
    private static final double FILL_FACTOR = 0.5;

    /*
        Free all data and set new capacity
        One method implementation because of operations simplicity
     */
    private void freeAndSetCapacity(int capacity) {
        this.capacity = capacity;
        size = 0;
        table = new MyList[capacity];
        Arrays.fill(table, new MyList());
    }

    public HashTable() {
        freeAndSetCapacity(START_CAPACITY);
    }

    private HashTable(int capacity) {
        freeAndSetCapacity(capacity);
    }

    private void copy(HashTable original) throws IllegalArgumentException {
        if (original == null) {
            throw new IllegalArgumentException("The object to be copied can not be null");
        }
        table = original.table;
        capacity = original.capacity;
        size = original.size;
    }

    // Doubles the capacity of HashTable
    private void rebuild() {
        HashTable doubleHashTable = new HashTable(capacity * 2);
        for (MyList chain : table) {
            for (var it : chain) {
                MyPair vertex = (MyPair) it;
                doubleHashTable.put(vertex.first, vertex.second);
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

    public boolean contains(String key) {
        if (key == null) {
            return false;
        }
        int position = getPosition(key);
        return table[position].contains(key);
    }

    public String get(String key) {
        if (key == null) {
            return null;
        }
        int position = getPosition(key);
        return table[position].get(key);
    }

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
        if (size > FILL_FACTOR * capacity) {
            rebuild();
        }
        return previousValue;
    }

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
        freeAndSetCapacity(START_CAPACITY);
    }
}

