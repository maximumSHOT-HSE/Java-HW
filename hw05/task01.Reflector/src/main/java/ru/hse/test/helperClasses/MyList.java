package ru.hse.test.helperClasses;

import java.util.Arrays;
import java.util.Iterator;

/** Dynamic array */
public class MyList implements Iterable {

    @Override
    public Iterator iterator() {
        return new Iterator() {

            private int currentPosition;

            @Override
            public boolean hasNext() {
                return currentPosition < size();
            }

            @Override
            public Object next() {
                Object nxt = list[currentPosition];
                currentPosition++;
                return nxt;
            }
        };
    }

    private static final int START_CAPACITY = 10;
    private MyPair[] list = new MyPair[START_CAPACITY];
    private int listSize;
    private int listCapacity = START_CAPACITY;

    public int size() {
        return listSize;
    }

    private int capacity() {
        return listCapacity;
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
            if (list[i].first.equals(key)) {
                return list[i].second;
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
            if (list[i].first.equals(key)) {
                String previousValue = list[i].second;
                list[i].second = value;
                return previousValue;
            }
        }
        if (size() == capacity()) {
            reserve(capacity() * 2);
        }
        list[listSize++] = new MyPair(key, value);
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
            if (list[i].first.equals(key)) {
                // swap(list[i], list.back()) with delete list.back()
                var bufVertex = list[i];
                list[i] = list[size() - 1];
                list[size() - 1] = null;
                listSize--;
                return bufVertex.second;
            }
        }
        return null;
    }

    public void clear() {
        Arrays.fill(list, null);
        listSize = 0;
    }
}