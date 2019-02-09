package ru.hse.surkov.hw03;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Balanced search tree implemented as
 * cartesian tree (BST of keys, Heap of priorities)
 * of pairs (key, random).
 * */
public final class Treap<E> extends AbstractSet implements MyTreeSet {

    private static final Random generator = new Random(153);

    private class DataHolder {
        @Nullable private Node root;
        private long version;
        @NotNull Comparator<? super E> comparator;

        @SuppressWarnings("unchecked")
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

    @NotNull private DataHolder data;
    private boolean isAscendingTreapOrder;
    @NotNull private Treap<E> reversedTreap;

    /*
    * Needs for constructing two Treaps with links, which points to the same Node (root)
    * for memory saving in case when method DescendingSet is used too frequently
    * */
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
        private long trackedVersion;

        public TreapIterator() {
            trackedVersion = data.version;
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
            if (trackedVersion != data.version) {
                throw new ConcurrentModificationException();
            }
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node buffered = currentNode;
            currentNode = isAscendingTreapOrder ? getNext(currentNode) : getPrev(currentNode);
            return buffered.value; // can not be null, because hasNext() == true
        }

        @Override
        public void remove() {
            if (trackedVersion != data.version) {
                throw new ConcurrentModificationException();
            }
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
            data.root = Treap.this.removeNode(candidate);
            data.version++;
            trackedVersion++;
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
        if (!isAscendingTreapOrder) {
            return descendingSet().last();
        }
        return data.root == null ? null : moveDeepLeft(data.root).value;
    }

    /** {@link java.util.TreeSet#last()} */
    @Override
    @Nullable
    public Object last() {
        if (!isAscendingTreapOrder) {
            return descendingSet().first();
        }
        return data.root == null ? null : moveDeepRight(data.root).value;
    }

    // <
    /** {@link java.util.TreeSet#lower(Object)} */
    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public Object lower(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().higher(o);
        }
        E need = (E) o;
        Node visitor = floorNode(data.root, need, data.comparator);
        if (visitor == null || data.comparator.compare(visitor.value, need) < 0) {
            return visitor == null ? null : visitor.value;
        }
        visitor = getPrev(visitor);
        return visitor == null ? null : visitor.value;
    }

    // <=
    /** {@link java.util.TreeSet#floor(Object)} */
    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public Object floor(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().ceiling(o);
        }
        Node visitor = floorNode(data.root, (E) o, data.comparator);
        return visitor == null ? null : visitor.value;
    }

    // >
    /** {@link java.util.TreeSet#higher(Object)} */
    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public Object higher(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().lower(o);
        }
        E need = (E) o;
        Node visitor = ceilingNode(data.root, need, data.comparator);
        if (visitor == null || data.comparator.compare(visitor.value, need) > 0) {
            return visitor == null ? null : visitor.value;
        }
        visitor = getNext(visitor);
        return visitor == null ? null : visitor.value;
    }

    // >=
    /** {@link java.util.TreeSet#ceiling(Object)} */
    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public Object ceiling(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().floor(o);
        }
        Node visitor = ceilingNode(data.root, (E) o, data.comparator);
        return visitor == null ? null : visitor.value;
    }

    /** {@link java.util.TreeSet#contains(Object)} */
    @Override
    public boolean contains(@NotNull Object o) {
        Object candidate = floor(o);
        return Objects.equals(candidate, o);
    }

    /** {@link java.util.TreeSet#add(Object)} */
    @SuppressWarnings("unchecked")
    @Override
    public boolean add(@NotNull Object o) {
        if (contains(o)) {
            return false;
        }
        data.version++;
        E need = (E) o;
        Pair<Node, Node> splitted = split(data.root, need);
        data.root = merge(merge(splitted.first, new Node(need)), splitted.second);
        return true;
    }

    /** {@link java.util.TreeSet#remove(Object)} */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(@NotNull Object o) {
        if (!contains(o)) {
            return false;
        }
        data.version++;
        data.root = remove(data.root, (E) o, data.comparator);
        return true;
    }

    private class Node {
        @Nullable private Node left;
        @Nullable private Node right;
        @Nullable private Node parent;
        @NotNull E value;
        long priority;
        int subtreeSize; // including current Node

        Node(@NotNull E value) {
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

    }

    // Recalculates vertex fields.
    private void updateNode(@Nullable Node vertex, boolean deleteParent) {
        if (vertex == null) {
            return;
        }
        if (deleteParent) {
            vertex.parent = null;
        }
        vertex.subtreeSize = 1;
        if (vertex.left != null) {
            vertex.subtreeSize += vertex.left.subtreeSize;
            vertex.left.parent = vertex;
        }
        if (vertex.right != null) {
            vertex.subtreeSize += vertex.right.subtreeSize;
            vertex.right.parent = vertex;
        }
    }

    @Nullable private Node getNodeParent(@Nullable Node vertex) {
        return vertex == null ? null : vertex.parent;
    }

    /*
     * Splits treap into two parts:
     * part with keys less than separator and
     * part with keys greater or equal then separator
     * without copying.
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
        updateNode(splitted.first, true);
        updateNode(splitted.second, true);
        return splitted;
    }

    /*
     * Merges two treaps into one without copying.
     * If not all keys in left treap are less then all keys in right treap,
     * then result of merge will be undefined.
     * */
    @Nullable private Node merge(@Nullable Node fromLeft, @Nullable Node fromRight) {
        Node merged;
        if (fromLeft == null || fromRight == null) {
            merged = fromLeft == null ? fromRight : fromLeft;
        } else {
            if (fromLeft.priority < fromRight.priority) {
                merged = merge(fromLeft.right, fromRight);
                fromLeft.right = merged;
                merged = fromLeft;
            } else {
                merged = merge(fromLeft, fromRight.left);
                fromRight.left = merged;
                merged = fromRight;
            }
        }
        updateNode(merged, true);
        return merged;
    }

    @Contract("!null -> !null")
    @Nullable private Node moveDeepLeft(@Nullable Node vertex) {
        if (vertex == null) {
            return null;
        }
        Node visitor = vertex;
        while (visitor.left != null) {
            visitor = visitor.left;
        }
        return visitor;
    }

    @Contract("!null -> !null")
    @Nullable private Node moveDeepRight(@Nullable Node vertex) {
        if (vertex == null) {
            return null;
        }
        Node visitor = vertex;
        while (visitor.right != null) {
            visitor = visitor.right;
        }
        return visitor;
    }

    @Nullable private Node getPrev(@Nullable Node vertex) {
        if (vertex == null) {
            return null;
        }
        if (vertex.left != null) {
            return moveDeepRight(vertex.left);
        }
        Node visitor = vertex;
        while (visitor != null && !visitor.isRightSon()) {
            visitor = visitor.parent;
        }
        return getNodeParent(visitor);
    }

    @Nullable private Node getNext(@Nullable Node vertex) {
        if (vertex == null) {
            return null;
        }
        if (vertex.right != null) {
            return moveDeepLeft(vertex.right);
        }
        Node visitor = vertex;
        while (visitor != null && !visitor.isLeftSon()) {
            visitor = visitor.parent;
        }
        return getNodeParent(visitor);
    }

    private int getOrder(@Nullable Node vertex, @NotNull E o, @NotNull Comparator<? super E> comparator) {
        return vertex == null ? 0 : comparator.compare(vertex.value, o);
    }

    @Nullable private Node floorNode(@Nullable Node vertex, @NotNull E o, @NotNull Comparator<? super E> comparator) {
        int order = getOrder(vertex, o, comparator);
        if (order == 0) {
            return vertex;
        }
        if (order > 0) {
            return floorNode(vertex.left, o, comparator); // vertex can not be null, because order != 0.
        }
        Node candidate = floorNode(vertex.right, o, comparator);
        if (candidate == null) {
            candidate = vertex;
        }
        return candidate;
    }

    @Nullable private Node ceilingNode(@Nullable Node vertex, @NotNull E o, @NotNull Comparator<? super E> comparator) {
        int order = getOrder(vertex, o, comparator);
        if (order == 0) {
            return vertex;
        }
        if (order < 0) {
            return ceilingNode(vertex.right, o, comparator); // vertex can not be null, because order != 0.
        }
        Node candidate = ceilingNode(vertex.left, o, comparator);
        if (candidate == null) {
            candidate = vertex;
        }
        return candidate;
    }

    /*
     * Removes node by 'pointer', merges it's sons without copying
     * and returns new root.
     * */
    @Nullable private Node removeNode(@Nullable Node vertex) {
        if (vertex == null) {
            return null;
        }
        Node merged = merge(vertex.left, vertex.right);
        if (vertex.parent == null) {
            return merged;
        }
        if (vertex.isLeftSon()) {
            vertex.parent.left = merged;
        } else if (vertex.isRightSon()) {
            vertex.parent.right = merged;
        }
        while (vertex.parent != null) {
            updateNode(vertex.parent, false);
            vertex = vertex.parent;
        }
        return vertex;
    }

    /*
     * Removes element o if such exists and returns new root.
     * If there was not such element null will be returned.
     * */
    @Nullable private Node remove(@Nullable Node vertex, @NotNull E o, @NotNull Comparator<? super E> comparator) {
        if (vertex == null) {
            return null;
        }
        int order = comparator.compare(vertex.value, o);
        if (order == 0) {
            return removeNode(vertex);
        }
        return remove(order < 0 ? vertex.right : vertex.left, o, comparator);
    }
}
