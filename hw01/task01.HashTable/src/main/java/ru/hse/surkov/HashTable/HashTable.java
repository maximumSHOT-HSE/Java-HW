package ru.hse.surkov.HashTable;

import ru.hse.surkov.List.List;

import java.lang.reflect.Array;
import java.util.Arrays;

public class HashTable {

    private List[] table;
    private int tableSize;    // physical size of table
    private int numberOfKeys; // actual number of keys

    // fill factor = NUM / DENUM
    private static final int NUM   = 1;
    private static final int DENUM = 2;

    private void clearAndSetTableSize(int tableSize) {
        this.tableSize = tableSize;
        numberOfKeys   = 0;
        table          = new List[tableSize];
        Arrays.fill(table, new List());
    }

    public HashTable() {
        this(1);
    }

    private HashTable(int tableSize) {
        clearAndSetTableSize(tableSize);
    }

    private void copy(HashTable original) {
        table        = original.table;
        tableSize    = original.tableSize;
        numberOfKeys = original.numberOfKeys;
    }

    /*
     * Doubles the tableSize of HashTable
     * */
    private void rebuild() {
        HashTable doubleHashTable = new HashTable(tableSize * 2);
        for (List chain : table) {
            List.ListVertex[] records = chain.getRecords();
            for (List.ListVertex vertex : records) {
                doubleHashTable.put(vertex.getKey(), vertex.getValue());
            }
        }
        copy(doubleHashTable);
    }

    private int getPosition(String key) {
        int position = key.hashCode() % tableSize;
        if (position < 0) {
            position += tableSize;
        }
        return position;
    }

    public int size() {
        return numberOfKeys;
    }

    public boolean contains(String key) {
        int position = getPosition(key);
        return table[position].contains(key);
    }

    /**
     * {@link ru.hse.surkov.List.List#get(String)}
     * */
    public String get(String key) {
        int position = getPosition(key);
        return table[position].get(key);
    }

    /**
     * {@link ru.hse.surkov.List.List#put(String, String)}
     * */
    public String put(String key, String value) {
        int position = getPosition(key);
        String previousValue = table[position].put(key, value);
        if (previousValue == null) {
            numberOfKeys++;
        }
        if (size() * (long)DENUM > tableSize * (long)NUM) { // size() > tableSize * (NUM / DENUM). Calculations without loss of accuracy
            rebuild();
        }
        return previousValue;
    }

    /**
     * {@link ru.hse.surkov.List.List#remove(String)}
     * */
    public String remove(String key) {
        int position = getPosition(key);
        String value = table[position].remove(key);
        if (value != null) {
            numberOfKeys--;
        }
        return value;
    }

    public void clear() {
        clearAndSetTableSize(1);
    }
}
