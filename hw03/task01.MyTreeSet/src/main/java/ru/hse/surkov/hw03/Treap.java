package ru.hse.surkov.hw03;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Balanced search tree implemented as
 * cartesian tree (BST of keys, Heap of priorities)
 * of pairs (key, random).
 * */
public final class Treap<E> extends AbstractSet implements MyTreeSet {

    private class DataHolder {
        @Nullable private RandomNode<E> root;
        private long version;
        @NotNull Comparator<? super E> comparator;

        DataHolder() {
            root = (RandomNode<E>) RandomNode.NULL_NODE;
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

        @Nullable private RandomNode<E> currentNode;

        public TreapIterator() {
            if (data.root == null) {
                currentNode = null;
            } else {
                currentNode = (RandomNode<E>) (isAscendingTreapOrder ? data.root.moveDeepLeft() : data.root.moveDeepRight());
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
            RandomNode<E> buffered = currentNode;
            currentNode = (RandomNode<E>) (isAscendingTreapOrder ? currentNode.getNext() : currentNode.getPrev());
            return buffered.value;
        }

        @Override
        public void remove() {
            RandomNode<E> candidate;
            if (currentNode != null) {
                candidate = (RandomNode<E>) (isAscendingTreapOrder ? currentNode.getPrev() : currentNode.getNext());
            } else {
                if (data.root == null) {
                    throw new IllegalStateException();
                }
                candidate = (RandomNode<E>) (isAscendingTreapOrder ? data.root.moveDeepRight() : data.root.moveDeepLeft());
            }
            if (candidate == null) {
                throw new IllegalStateException();
            }
            data.root = candidate.removeNode();
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
        return data.root == null ? null : data.root.moveDeepLeft().value;
    }

    /** {@link java.util.TreeSet#last()} */
    @Override
    @Nullable
    public Object last() {
        if (!isAscendingTreapOrder) {
            return descendingSet().first();
        }
        return data.root == null ? null : data.root.moveDeepRight().value;
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
        RandomNode<E> visitor = (RandomNode<E>) data.root.floorNode(need, data.comparator);
        if (data.comparator.compare(visitor.value, need) < 0) {
            return visitor.value;
        }
        visitor = (RandomNode<E>) visitor.getPrev();
        return visitor.value;
    }

    // <=
    /** {@link java.util.TreeSet#floor(Object)} */
    @Override
    @Nullable
    public Object floor(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().ceiling(o);
        }
        RandomNode<E> visitor = (RandomNode<E>) data.root.floorNode((E) o, data.comparator);
        return visitor.value;
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
        RandomNode<E> visitor = (RandomNode<E>) data.root.ceilingNode(need, data.comparator);
        if (data.comparator.compare(visitor.value, need) > 0) {
            return visitor.value;
        }
        visitor = (RandomNode<E>) visitor.getNext();
        return visitor.value;
    }

    // >=
    /** {@link java.util.TreeSet#ceiling(Object)} */
    @Override
    @Nullable
    public Object ceiling(@NotNull Object o) {
        if (!isAscendingTreapOrder) {
            return descendingSet().floor(o);
        }
        RandomNode<E> visitor = (RandomNode<E>) data.root.ceilingNode((E) o, data.comparator);
        return visitor.value;
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
        var splitted = data.root.split(need, data.comparator);
        data.root = RandomNode.merge(RandomNode.merge(splitted.first, new RandomNode<E>(need)), splitted.second);
        return true;
    }

    /** {@link java.util.TreeSet#remove(Object)} */
    @Override
    public boolean remove(@NotNull Object o) {
        if (!contains(o)) {
            return false;
        }
        data.root = data.root.remove((E) o, data.comparator);
        return true;
    }
}
