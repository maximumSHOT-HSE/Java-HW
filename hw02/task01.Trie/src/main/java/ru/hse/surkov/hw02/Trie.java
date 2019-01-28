package ru.hse.surkov.hw02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Data structure for storing a set of strings,
 * A tree with symbols on edges
 * Data structure does not contain null's
 * */
public class Trie implements Serializable {

    /**
     * Adds a new string (if there was not such element)
     * with O(|element|) time complexity
     * Methods does nothing
     * @return true if element != null and there was not such element,
     * false -- if there was or element == null
     * */
    public boolean add(String element) {
        if (element == null) {
            return false;
        }
    }

    /**
     * Checks if there is such element or not
     * with O(|element|) time complexity
     * @return true if exists, false -- if not exists
     * */
    public boolean contains(String element) {

    }

    /**
     * Removes an element if it exists
     * with O(|element|) time complexity
     * @return true if such element there was, false -- if there was not
     * */
    public boolean remove(String element) {

    }

    /**
     * O(1) time complexity
     * @return number of strings
     * */
    public int size() {

    }

    /**
     * O(|prefix|) time complexity
     * @return the number of strings, which starts with such prefix
     * */
    public int howManyStartWithPrefix(String prefix) {

    }

    /**
     * Transfers Trie to the output stream
     * @throws IOException if there is IO problems during transferring
     * */
    @Override
    public void serialize(OutputStream out) throws IOException {

    }

    /**
     * Replaces current Trie with data from input stream
     * @throws IOException if there is IO problems during retrieving
     * */
    @Override
    public void deserialize(InputStream in) throws IOException {

    }
}
