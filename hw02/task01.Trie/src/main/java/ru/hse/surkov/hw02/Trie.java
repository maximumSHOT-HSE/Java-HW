package ru.hse.surkov.hw02;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Data structure for storing a set of strings,
 * A tree with symbols on edges
 * Data structure does not contain null's
 * */
public class Trie implements Serializable {

    private class Node implements Serializable {

        private int cntLeafsInSubTree;
        private HashMap<Character, Node> arc;
        private Node parent;
        private char parentChar;
        private boolean isLeaf;

        /*
        * Recursive method
        * <size: int>
        * <isLeaf: int>
        * <symbol1: int> <target1: Node> ...
        * */
        @Override
        public void serialize(OutputStream out) throws IOException {
            out.write(arc.size());
            out.write(isLeaf() ? 1 : 0);
            for (Map.Entry<Character, Node> elem : arc.entrySet()) {
                char symbol = elem.getKey();
                Node target = elem.getValue();
                out.write(symbol);
                target.serialize(out);
            }
        }

        // Replaces current Node with data from input stream
        @Override
        public void deserialize(InputStream in) throws IOException {
           int size = in.read();
           zeroCntLeafsInSubtree();
           if (in.read() == 0) { // isLeaf
               turnOffLeaf();
           } else {
               turnOnLeaf();
               incLeafsCounter(+1);
           }
           if (size < 0) {
               throw new IOException("Size (number of arcs) can not be negative");
           }
           arc.clear();
           for (int iter = 0; iter < size; iter++) {
               char symbol = (char) in.read();
               Node target = new Node(symbol, this);
               target.deserialize(in);
               incLeafsCounter(+target.getCntLeafsInSubTree());
           }
        }

        private void zeroCntLeafsInSubtree() {
            cntLeafsInSubTree = 0;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }
            Node other = (Node) obj;
            if (
                (getCntLeafsInSubTree() != other.getCntLeafsInSubTree()) ||
                (getParentChar() != other.getParentChar()) ||
                (isLeaf() != other.isLeaf())
            ) {
                return false;
            }
            for (Map.Entry<Character, Node> elem : arc.entrySet()) {
                char symbol = elem.getKey();
                Node thisTarget = elem.getValue();
                Node otherTarget = other.next(symbol);
                if (!thisTarget.equals(otherTarget)) {
                    return false;
                }
            }
            return true;
        }

        public int hashcode() {
            int hashCode =
                Integer.hashCode(getCntLeafsInSubTree()) ^
                Character.hashCode(getParentChar()) ^
                Boolean.hashCode(isLeaf()) ^
                arc.hashCode();
            for (Map.Entry<Character, Node> elem : arc.entrySet()) {
                char symbol = elem.getKey();
                Node target = elem.getValue();
                hashCode ^= target.hashcode();
            }
            return hashCode;
        }

        public Node() {
            cntLeafsInSubTree = 0;
            arc = new HashMap<>();
            parent = null;
            parentChar = 0;
            isLeaf = false;
        }

        public Node(char symbol, Node parent) {
            cntLeafsInSubTree = 0;
            arc = new HashMap<>();
            this.parent = parent;
            parentChar = symbol;
            isLeaf = false;
            parent.addArc(symbol, this);
        }

        public Node next(char symbol) {
            return arc.get(symbol);
        }

        public void incLeafsCounter(int diff) {
            cntLeafsInSubTree += diff;
        }

        public int getCntLeafsInSubTree() {
            return cntLeafsInSubTree;
        }

        public void turnOnLeaf() {
            isLeaf = true;
        }

        public void turnOffLeaf() {
            isLeaf = false;
        }

        public char getParentChar() {
            return parentChar;
        }

        private void addArc(char symbol, Node target) {
            arc.put(symbol, target);
        }

        public void delArc(char symbol) {
            arc.remove(symbol);
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public Node getParent() {
            return parent;
        }
    }

    private Node root;

    public Trie() {
        root = new Node();
    }

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
        if (visitor.isLeaf()) {
            while (visitor != null) {
                visitor.incLeafsCounter(-1);
                visitor = visitor.getParent();
            }
            return false;
        } else {
            visitor.turnOnLeaf();
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
        return visitor != null && visitor.isLeaf();
    }

    /**
     * Removes an element if it exists
     * with O(|element|) time complexity
     * @return true if such element there was, false -- if there was not
     * */
    public boolean remove(String element) {
        Node visitor = moveTo(element);
        if (visitor == null || !visitor.isLeaf()) {
            return false;
        }
        visitor.turnOffLeaf();
        while (visitor != null) {
            visitor.incLeafsCounter(-1);
            Node buffer = visitor;
            visitor = visitor.getParent();
            if (buffer.getParent() != null && buffer.getCntLeafsInSubTree() == 0) {
                visitor.delArc(buffer.getParentChar());
            }
        }
        return true;
    }

    /**
     * O(1) time complexity
     * @return number of strings
     * */
    public int size() {
        return root.getCntLeafsInSubTree();
    }

    /**
     * O(|prefix|) time complexity
     * @return the number of strings, which starts with such prefix
     * */
    public int howManyStartWithPrefix(String prefix) {
        Node visitor = moveTo(prefix);
        return visitor == null ? 0 : visitor.getCntLeafsInSubTree();
    }

    /**
     * Transfers Trie to the output stream
     * @throws IOException if there is IO problems during transferring
     * */
    @Override
    public void serialize(OutputStream out) throws IOException {
        root.serialize(out);
    }

    /**
     * Replaces current Trie with data from input stream
     * @throws IOException if there is IO problems during retrieving
     * */
    @Override
    public void deserialize(InputStream in) throws IOException {
        Node inputRoot = new Node();
        inputRoot.deserialize(in);
        root = inputRoot;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Trie)) {
            return false;
        }
        Trie other = (Trie) obj;
        return root.equals(other.root);
    }

    public int hashCode() {
        return root.hashCode();
    }
}
