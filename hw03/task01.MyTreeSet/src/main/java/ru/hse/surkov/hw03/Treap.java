package ru.hse.surkov.hw03;

import com.sun.source.tree.Tree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Balanced search tree implemented as
 * cartesian tree (BST of keys, Heap of priorities)
 * of pairs (key, random)
 * */
public final class Treap<E> extends AbstractSet implements MyTreeSet {

    private static final Random generator = new Random(153);

    private class Node {
        @Nullable private Node left;
        @Nullable private Node right;
        @Nullable private Node parent;
        @NotNull E value;
        long priority;
        int subtreeSize; // including current Node

        Node(E value) {
            left = null;
            right = null;
            parent = null;
            this.value = value;
            priority = generator.nextLong();
            subtreeSize = 1;
        }

        boolean isLeftSon() {
            return parent != null && (parent.left == this);
        }

        boolean isRightSon() {
            return parent != null && (parent.right == this);
        }

        void update() {
            subtreeSize = 1;
            if (left != null) {
                subtreeSize += left.subtreeSize;
                left.parent = this;
            }
            if (right != null) {
                subtreeSize += right.subtreeSize;
                right.parent = this;
            }
        }

    }

    /*
    * Splits treap into two parts:
    * part with keys less than separator and
    * part with keys greater or equal then separator
    * without copying
    * (<)(>=)
    * */
    @NotNull private Pair<Node, Node> split(@Nullable Node vertex, @NotNull E separator) {
        if (vertex == null) {
            return new Pair<>(null, null);
        }
        Pair<Node, Node> splitted;
        if (data.comparator.compare(vertex.value, separator) < 0) {
            splitted = split(vertex.right, separator);
            vertex.right = splitted.first;
            splitted.first = vertex;
        } else {
            splitted = split(vertex.left, separator);
            vertex.left = splitted.second;
            splitted.second = vertex;
        }
        if (splitted.first != null) {
            splitted.first.update();
        }
        if (splitted.second != null) {
            splitted.second.update();
        }
        return splitted;
    }

    /*
    * Merges two treap into one without copying.
    * All keys in left treap are less then all keys in right treap
    * */
    @Nullable private Node merge(@Nullable Node fromLeft, @Nullable Node fromRight) {
        if (fromLeft == null || fromRight == null) {
            return fromLeft == null ? fromRight : fromLeft;
        }
        Node merged;
        if (fromLeft.priority < fromRight.priority) {
            merged = merge(fromLeft.right, fromRight);
            fromLeft.right = merged;
            merged = fromLeft;
        } else {
            merged = merge(fromLeft, fromRight.left);
            fromRight.left = merged;
            merged = fromRight;
        }
        merged.update();
        return merged;
    }

    @NotNull private Node moveDeepLeft(@NotNull Node vertex) {
        if (!isAscendingTreapOrder) {
            return reversedTreap.moveDeepRight(vertex);
        }
        Node visitor = vertex;
        while (visitor.left != null) {
            visitor = visitor.left;
        }
        return visitor;
    }

    @NotNull private Node moveDeepRight(@NotNull Node vertex) {
        if (!isAscendingTreapOrder) {
            return reversedTreap.moveDeepLeft(vertex);
        }
        Node visitor = vertex;
        while (visitor.right != null) {
            visitor = visitor.right;
        }
        return visitor;
    }

    @Nullable private Node getPrev(@NotNull Node vertex) {
        if (!isAscendingTreapOrder) {
            return reversedTreap.getNext(vertex);
        }
        if (vertex.left != null) {
            return moveDeepRight(vertex.left);
        }
        Node visitor = vertex;
        while (visitor != null && !visitor.isRightSon()) {
            visitor = visitor.parent;
        }
        return visitor == null ? null : visitor.parent;
    }

    @Nullable private Node getNext(@NotNull Node vertex) {
        if (!isAscendingTreapOrder) {
            return reversedTreap.getPrev(vertex);
        }
        if (vertex.right != null) {
            return moveDeepLeft(vertex.right);
        }
        Node visitor = vertex;
        while (visitor != null && !visitor.isLeftSon()) {
            visitor = visitor.parent;
        }
        return visitor == null ? null : visitor.parent;
    }

    private class DataHolder {
        @Nullable private Node root;
        private long version;
        @NotNull Comparator<? super E> comparator;

        DataHolder() {
            root = null;
            version = 0;
            comparator = (Comparator<? super E>) Comparator.naturalOrder();
        }

        DataHolder(@NotNull Comparator<? super E> comparator) {
            root = null;
            version = 0;
            this.comparator = comparator;
        }
    }

    private DataHolder data;
    private boolean isAscendingTreapOrder;
    @NotNull private Treap<E> reversedTreap;

    private Treap(@NotNull Treap<E> reversedTreap) {
        data = reversedTreap.data;
        isAscendingTreapOrder = !reversedTreap.isAscendingTreapOrder;
        this.reversedTreap = reversedTreap;
    }

    /** {@link java.util.TreeSet#TreeSet()} */
    public Treap() {
        data = new DataHolder();
        isAscendingTreapOrder = true;
        reversedTreap = new Treap<>(this);
    }

    /** {@link java.util.TreeSet#TreeSet(Comparator)} */
    public Treap(@NotNull Comparator<? super E> comparator) {
        data = new DataHolder(comparator);
        isAscendingTreapOrder = true;
        reversedTreap = new Treap<>(this);
    }

    private class TreapIterator implements Iterator {

        @Nullable private Node currentNode;

        public TreapIterator() {
            if (data.root == null) {
                currentNode = null;
            } else {
                currentNode = isAscendingTreapOrder ? moveDeepLeft(data.root) : moveDeepRight(data.root);
            }
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        @NotNull public Object next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node buffered = currentNode;
            currentNode = isAscendingTreapOrder ? getNext(currentNode) : getPrev(currentNode);
            return buffered.value;
        }

        @Override
        public void remove() {
            Node candidate;
            if (currentNode != null) {
                candidate = isAscendingTreapOrder ? getPrev(currentNode) : getNext(currentNode);
            } else {
                if (data.root == null) {
                    throw new IllegalStateException();
                }
                candidate = isAscendingTreapOrder ? moveDeepRight(data.root) : moveDeepLeft(data.root);
            }
            if (candidate == null) {
                throw new IllegalStateException();
            }
            Treap.this.removeNode(candidate);
        }
    }

    /** {@link java.util.TreeSet#iterator()} */
    @Override
    @NotNull
    public Iterator iterator() {
        return new TreapIterator();
    }

    /** {@link java.util.TreeSet#size()} */
    @Override
    public int size() {
        return data.root == null ? 0 : data.root.subtreeSize;
    }

    /** {@link java.util.TreeSet#descendingIterator()} */
    @Override
    @NotNull
    public Iterator descendingIterator() {
        return descendingSet().iterator();
    }

    /** {@link java.util.TreeSet#descendingSet()} */
    @Override
    @NotNull
    public MyTreeSet descendingSet() {
        return reversedTreap;
    }

    /** {@link java.util.TreeSet#first()} */
    @Override
    @Nullable
    public Object first() {
        return data.root == null ? null : moveDeepLeft(data.root).value;
    }

    /** {@link java.util.TreeSet#last()} */
    @Override
    @Nullable
    public Object last() {
        return descendingSet().first();
    }

    @Nullable private Node floorNode(@Nullable Node vertex, @NotNull E o) {
        if (vertex == null) {
            return null;
        }
        int order = data.comparator.compare(vertex.value, o);
        if (order == 0) {
            return vertex;
        }
        if (order > 0) {
            return floorNode(vertex.left, o);
        }
        Node candidate = floorNode(vertex.right, o);
        if (candidate == null) {
            candidate = vertex;
        }
        return candidate;
    }

    @Nullable private Node ceilingNode(@Nullable Node vertex, @NotNull E o) {
        if (vertex == null) {
            return null;
        }
        int order = data.comparator.compare(vertex.value, o);
        if (order == 0) {
            return vertex;
        }
        if (order < 0) {
            return ceilingNode(vertex.right, o);
        }
        Node candidate = ceilingNode(vertex.left, o);
        if (candidate == null) {
            candidate = vertex;
        }
        return candidate;
    }

    // <
    /** {@link java.util.TreeSet#lower(Object)} */
    @Override
    @Nullable
    public Object lower(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().higher(o);
        }
        E need = (E) o;
        Node visitor = floorNode(data.root, need);
        if (visitor == null || data.comparator.compare(visitor.value, need) < 0) {
            return visitor == null ? visitor : visitor.value;
        }
        visitor = getPrev(visitor);
        return visitor == null ? null : visitor.value;
    }

    // <=
    /** {@link java.util.TreeSet#floor(Object)} */
    @Override
    @Nullable
    public Object floor(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().ceiling(o);
        }
        Node visitor = floorNode(data.root, (E) o);
        return visitor == null ? null : visitor.value;
    }

    // >
    /** {@link java.util.TreeSet#higher(Object)} */
    @Override
    @Nullable
    public Object higher(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().lower(o);
        }
        E need = (E) o;
        Node visitor = ceilingNode(data.root, need);
        if (visitor == null || data.comparator.compare(visitor.value, need) > 0) {
            return visitor == null ? null : visitor.value;
        }
        visitor = getNext(visitor);
        return visitor == null ? visitor : visitor.value;
    }

    // >=
    /** {@link java.util.TreeSet#ceiling(Object)} */
    @Override
    @Nullable
    public Object ceiling(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().floor(o);
        }
        Node visitor = ceilingNode(data.root, (E) o);
        return visitor == null ? null : visitor.value;
    }

    /** {@link java.util.TreeSet#contains(Object)} */
    @Override
    public boolean contains(@NotNull Object o) {
        Object candidate = floor(o);
        return Objects.equals(candidate, o);
    }

    /** {@link java.util.TreeSet#add(Object)} */
    @Override
    public boolean add(@NotNull Object o) {
        if (contains(o)) {
            return false;
        }
        E need = (E) o;
        Pair<Node, Node> splitted = split(data.root, need);
        data.root = merge(merge(splitted.first, new Node(need)), splitted.second);
        return true;
    }

    /*
    * Removes node by 'pointer', merges it's sons without copying
    * and returns new merged Node
    * */
    @Nullable private Node removeNode(@NotNull Node vertex) {
        Node merged = merge(vertex.left, vertex.right);
        if (vertex.isLeftSon()) {
            vertex.parent.left = merged;
        } else if (vertex.isRightSon()) {
            vertex.parent.right = merged;
        }
        while (vertex.parent != null) {
            vertex.parent.update();
            vertex = vertex.parent;
        }
        return merged;
    }

    @Nullable private Node remove(@Nullable Node vertex, @NotNull E o) {
        if (vertex == null) {
            return null;
        }
        int order = data.comparator.compare(vertex.value, o);
        if (order == 0) {
            return removeNode(vertex);
        }
        if (order < 0) {
            remove(vertex.right, o);
        } else if(order > 0) {
            remove(vertex.left, o);
        }
        return vertex;
    }

    /** {@link java.util.TreeSet#remove(Object)} */
    @Override
    public boolean remove(@NotNull Object o) {
        if (!contains(o)) {
            return false;
        }
        data.root = remove(data.root, (E) o);
        return true;
    }
}
