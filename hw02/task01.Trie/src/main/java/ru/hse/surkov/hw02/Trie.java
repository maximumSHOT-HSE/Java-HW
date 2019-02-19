package ru.hse.surkov.hw02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Data structure for storing a set of strings,
 * A tree with symbols on edges
 * Data structure does not contain null's
 * */
public final class Trie implements Serializable {

    private Node root = new Node();

    /**
     * Adds a new string (if there was not such element)
     * with O(|element|) time complexity
     * Methods does nothing if there was such element
     * @return true if element != null and there was not such element,
     * false -- if there was or element == null
     * */
    public boolean add(String element) {
        if (element == null) {
            return false;
        }
        root.incLeafsCounter(+1);
        Node visitor = root;
        for (int i = 0; i < element.length(); i++) {
            char symbol = element.charAt(i);
            Node target = visitor.next(symbol);
            if (target == null) {
                target = new Node(symbol, visitor);
            }
            visitor = visitor.next(symbol);
            visitor.incLeafsCounter(+1);
        }
        if (visitor.isLeaf) {
            while (visitor != null) {
                visitor.incLeafsCounter(-1);
                visitor = visitor.parent;
            }
            return false;
        } else {
            visitor.isLeaf = true;
            return true;
        }
    }

    private Node moveTo(String element) {
        if (element == null) {
            return null;
        }
        Node visitor = root;
        for (int i = 0; i < element.length(); i++) {
            char symbol = element.charAt(i);
            Node target = visitor.next(symbol);
            if (target == null) {
                return null;
            }
            visitor = target;
        }
        return visitor;
    }

    /**
     * Checks if there is such element or not
     * with O(|element|) time complexity
     * @return true if exists, false -- if not exists
     * */
    public boolean contains(String element) {
        Node visitor = moveTo(element);
        return visitor != null && visitor.isLeaf;
    }

    /**
     * Removes an element if it exists
     * with O(|element|) time complexity
     * @return true if such element there was, false -- if there was not
     * */
    public boolean remove(String element) {
        Node visitor = moveTo(element);
        if (visitor == null || !visitor.isLeaf) {
            return false;
        }
        visitor.isLeaf = false;
        while (visitor != null) {
            visitor.incLeafsCounter(-1);
            Node buffer = visitor;
            visitor = visitor.parent;
            if (buffer.parent != null && buffer.subtreeLeafsCount == 0) {
                visitor.delArc(buffer.parentChar);
            }
        }
        return true;
    }

    /**
     * O(1) time complexity
     * @return number of strings
     * */
    public int size() {
        return root.subtreeLeafsCount;
    }

    /**
     * O(|prefix|) time complexity
     * @return the number of strings, which starts with such prefix
     * */
    public int howManyStartWithPrefix(String prefix) {
        Node visitor = moveTo(prefix);
        return visitor == null ? 0 : visitor.subtreeLeafsCount;
    }

    /** {@link Serializable#serialize(OutputStream)} */
    @Override
    public void serialize(OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("Output stream for serializing must not be null");
        }
        root.serialize(out);
    }

    /** {@link Serializable#deserialize(InputStream)} */
    @Override
    public void deserialize(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("Input stream for deserializing must not be null");
        }
        Node inputRoot = new Node();
        inputRoot.deserialize(in);
        root = inputRoot;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Trie)) {
            return false;
        }
        Trie other = (Trie) obj;
        return root.equals(other.root);
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    private class Node implements Serializable {

        private int subtreeLeafsCount;
        private HashMap<Character, Node> arcsToChildren = new HashMap<>();
        private Node parent;
        private char parentChar;
        private boolean isLeaf;

        Node() {

        }

        Node(char symbol, Node parent) {
            this.parent = parent;
            parentChar = symbol;
            parent.addArc(symbol, this);
        }

        /*
         * Serializing of Node in following way:
         * amount of outgoing arcs to children (int),
         * flag, which shows whether current node is leaf or not (boolean)
         * list of outgoing symbols and similar description of children (symbol1, (...children1...), ...)
         * */
        @Override
        public void serialize(OutputStream out) throws IOException {
            if (out == null) {
                throw new IllegalArgumentException("Output stream for serializing must not be null");
            }
            out.write(arcsToChildren.size());
            out.write(isLeaf ? 1 : 0);
            for (var elem : arcsToChildren.entrySet()) {
                char symbol = elem.getKey();
                Node target = elem.getValue();
                out.write(symbol);
                target.serialize(out);
            }
        }

        @Override
        public void deserialize(InputStream in) throws IOException {
            if (in == null) {
                throw new IllegalArgumentException("Input stream for deserializing must not be null");
            }
            int size = in.read();
            subtreeLeafsCount = 0;
            if (in.read() == 0) { // isLeaf
                isLeaf = false;
            } else {
                isLeaf = true;
                incLeafsCounter(+1);
            }
            if (size < 0) {
                throw new IOException("Size (number of arcsToChildren) can not be negative");
            }
            arcsToChildren.clear();
            for (int iter = 0; iter < size; iter++) {
                char symbol = (char) in.read();
                Node target = new Node(symbol, this);
                target.deserialize(in);
                incLeafsCounter(+target.subtreeLeafsCount);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }
            Node other = (Node) obj;
            if ((subtreeLeafsCount != other.subtreeLeafsCount) ||
                    (parentChar != other.parentChar) ||
                    (isLeaf != other.isLeaf)) {
                return false;
            }
            for (var elem : arcsToChildren.entrySet()) {
                char symbol = elem.getKey();
                Node thisTarget = elem.getValue();
                Node otherTarget = other.next(symbol);
                if (!thisTarget.equals(otherTarget)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashCode = Integer.hashCode(subtreeLeafsCount) ^
                    Character.hashCode(parentChar) ^
                    Boolean.hashCode(isLeaf) ^
                    arcsToChildren.hashCode();
            for (var elem : arcsToChildren.entrySet()) {
                Node target = elem.getValue();
                hashCode ^= target.hashCode();
            }
            return hashCode;
        }

        private Node next(char symbol) {
            return arcsToChildren.get(symbol);
        }

        private void incLeafsCounter(int diff) {
            subtreeLeafsCount += diff;
        }

        private void addArc(char symbol, Node target) {
            arcsToChildren.put(symbol, target);
        }

        private void delArc(char symbol) {
            arcsToChildren.remove(symbol);
        }
    }
}
