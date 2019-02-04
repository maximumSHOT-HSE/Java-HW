package ru.hse.surkov.hw03;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;

/**
 * Balanced search tree implemented as
 * cartesian tree (BST of keys, Heap of priorities)
 * of pairs (key, random)
 * */
public final class Treap<E> extends AbstractSet implements MyTreeSet {

    private class Node {
        private Node left;
        private Node right;
        E value;
        long priority;
    }

    private class DataHolder {
        @Nullable private Node root;
        private long version;
        @NotNull Comparator<? super E> comparator;

        public DataHolder() {
            root = null;
            version = 0;
            comparator = (Comparator<? super E>) Comparator.naturalOrder();
        }

        public DataHolder(@NotNull Comparator<? super E> comparator) {
            root = null;
            version = 0;
            this.comparator = comparator;
        }
    }

    private DataHolder data;
    private boolean isAscendingOrder;
    @NotNull private Treap<E> reversedTreap;

    private Treap(@NotNull Treap<E> reversedTreap) {
        data = reversedTreap.data;
        isAscendingOrder = !reversedTreap.isAscendingOrder;
        this.reversedTreap = reversedTreap;
    }

    /** {@link java.util.TreeSet#TreeSet()} */
    public Treap() {
        data = new DataHolder();
        isAscendingOrder = true;
        reversedTreap = new Treap<>(this);
    }

    /** {@link java.util.TreeSet#TreeSet(Comparator)} */
    public Treap(@NotNull Comparator<? super E> comparator) {
        data = new DataHolder(comparator);
        isAscendingOrder = true;
        reversedTreap = new Treap<>(this);
    }

    /** {@link java.util.TreeSet#iterator()} */
    @Override
    @NotNull
    public Iterator iterator() {
        return new Iterator() {
            @Override
            public boolean hasNext() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object next() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /** {@link java.util.TreeSet#size()} */
    @Override
    public int size() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /** {@link java.util.TreeSet#last()} */
    @Override
    @Nullable
    public Object last() {
        return descendingSet().first();
    }

    // <
    /** {@link java.util.TreeSet#lower(Object)} */
    @Override
    @Nullable
    public Object lower(@NotNull Object o) {
        throw new UnsupportedOperationException();
    }

    // <=
    /** {@link java.util.TreeSet#floor(Object)} */
    @Override
    @Nullable
    public Object floor(@NotNull Object o) {
        throw new UnsupportedOperationException();
    }

    // >
    /** {@link java.util.TreeSet#higher(Object)} */
    @Override
    @Nullable
    public Object higher(@NotNull Object o) {
        return descendingSet().lower(o);
    }

    // >=
    /** {@link java.util.TreeSet#ceiling(Object)} */
    @Override
    @Nullable
    public Object ceiling(@NotNull Object o) {
        return descendingSet().floor(o);
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
        throw new UnsupportedOperationException();
    }

    /** {@link java.util.TreeSet#remove(Object)} */
    @Override
    public boolean remove(@NotNull Object o) {
        throw new UnsupportedOperationException();
    }
}
