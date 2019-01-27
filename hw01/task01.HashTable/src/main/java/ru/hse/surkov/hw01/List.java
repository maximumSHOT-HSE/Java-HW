package ru.hse.surkov.hw01;

import java.util.Arrays;

/** Dynamic array */
public class List {

    public static class ListVertex {
        private String key;
        private String value;

        public ListVertex(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private ListVertex[] list;
    private int listSize;
    private int listCapacity;
    private static final int START_CAPACITY = 10;

    public List() {
        listSize = 0;
        listCapacity = START_CAPACITY;
        list = new ListVertex[listCapacity];
    }

    private int size() {
        return listSize;
    }

    private int capacity() {
        return listCapacity;
    }

    public ListVertex[] getRecords() {
        return Arrays.copyOf(list, listSize);
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * Method returns a value by key (if there is no such key, then answer will be null).
     * if there is such key, then it is present in a single copy.
     * get(null) == null
     * @return value of key or null (if there is no such key)
     * */
    public String get(String key) {
        if (key == null) {
            return null;
        }
        for (int i = 0; i < size(); i++) {
            if (list[i].getKey().equals(key)) {
                return list[i].getValue();
            }
        }
        return null;
    }

    /*
        Method changes listCapacity
        if oldCapacity < capacity then data will be saved
        else data in [0, capacity) will be saved
     */
    private void reserve(int capacity) {
        list = Arrays.copyOf(list, capacity);
        listCapacity = capacity;
        listSize = Math.min(capacity, listSize);
    }

    /**
     * Method puts value by key (if such key has already been, then previous value will be overwritten)
     * @return previous value of key or null (if there was no such key)
     * @throws IllegalArgumentException if (key == null) or (value == null)
     * */
    public String put(String key, String value) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key can not be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        for (int i = 0; i < size(); i++) {
            if (list[i].getKey().equals(key)) {
                String previousValue = list[i].getValue();
                list[i].setValue(value);
                return previousValue;
            }
        }
        if (size() == capacity()) {
            reserve(capacity() * 2);
        }
        list[listSize++] = new ListVertex(key, value);
        return null;
    }

    /**
     * Method removes value by key
     * and does nothing if there is no such key.
     * remove(null) == null
     * @return value by key (or null if there was no such key)
     * */
    public String remove(String key) {
        if (key == null) {
            return null;
        }
        for (int i = 0; i < size(); i++) {
            if (list[i].getKey().equals(key)) {
                // swap(list[i], list.back())
                ListVertex bufVertex = list[i];
                list[i] = list[size() - 1];
                list[size() - 1] = bufVertex;
                listSize--;
                return bufVertex.getValue();
            }
        }
        return null;
    }

    public void clear() {
        listSize = 0;
    }
}
