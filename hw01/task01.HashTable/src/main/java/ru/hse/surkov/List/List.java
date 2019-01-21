package ru.hse.surkov.List;

import java.lang.reflect.Array;
import java.util.Arrays;

public class List {

    public static class ListVertex {
        private String key, value;

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

    public List() {
        listSize     = 0;
        listCapacity = 1;
        list         = new ListVertex[listCapacity];
    }

    public int size() {
        return listSize;
    }

    public int capacity() {
        return listCapacity;
    }

    public ListVertex[] getRecords() {
        return Arrays.copyOf(list, listSize);
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    /**
     * @return value of key or null (if there is no such key)
     * */
    public String get(String key) {
        for (int i = 0;i < size();i++) {
            if (list[i].getKey().equals(key)) {
                return list[i].getValue();
            }
        }
        return null;
    }

    /*
    * Method changes listCapacity
    * */
    public void reserve(int capacity) {
        list         = Arrays.copyOf(list, capacity);
        listCapacity = capacity;
        listSize     = Math.min(capacity, listSize);
    }

    /**
     * Method puts value by key (if such key has already been, then previous value will be overwritten)
     * @return previous value of key or null (if there was no such key)
     * */
    public String put(String key, String value) {
        for (int i = 0;i < size();i++) {
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
     * and does nothing if there is not such key
     * @return value by key (or null if there was no such key)
     * */
    public String remove(String key) {
        for (int i = 0;i < size();i++) {
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
