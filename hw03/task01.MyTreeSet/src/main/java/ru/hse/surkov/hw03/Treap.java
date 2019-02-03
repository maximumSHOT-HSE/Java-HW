package ru.hse.surkov.hw03;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Balanced search tree implemented as
 * cartesian tree (BST of keys, Heap of params)
 * of pairs (key, random)
 * */
public class Treap<E> extends AbstractSet implements MyTreeSet {

    /** {@link java.util.TreeSet#TreeSet()} */
    public Treap() {
    }

    /** {@link java.util.TreeSet#TreeSet(Comparator)} */
    public Treap(Comparator<? super E> comparator) {

    }

    /** {@link java.util.TreeSet#iterator()} */
    @Override
    @NotNull
    public Iterator iterator() {
        return new Iterator() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }
        };
    }

    /** {@link java.util.TreeSet#size()} */
    @Override
    public int size() {
        return 0;
    }

    /** {@link java.util.TreeSet#descendingIterator()} */
    @Override
    @NotNull
    public Iterator descendingIterator() {
        return null;
    }

    /** {@link java.util.TreeSet#descendingSet()} */
    @Override
    @NotNull
    public MyTreeSet descendingSet() {
        return null;
    }

    /** {@link java.util.TreeSet#first()} */
    @Override
    @Nullable
    public Object first() {
        return null;
    }

    /** {@link java.util.TreeSet#last()} */
    @Override
    @Nullable
    public Object last() {
        return null;
    }

    /** {@link java.util.TreeSet#lower(Object)} */
    @Override
    @Nullable
    public Object lower(@NotNull Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#floor(Object)} */
    @Override
    @Nullable
    public Object floor(@NotNull Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#ceiling(Object)} */
    @Override
    @Nullable
    public Object ceiling(@NotNull Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#higher(Object)} */
    @Override
    @Nullable
    public Object higher(@NotNull Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#contains(Object)} */
    @Override
    public boolean contains(@NotNull Object o) {
        return super.contains(o);
//        return false;
    }

    /** {@link java.util.TreeSet#add(Object)} */
    @Override
    public boolean add(@NotNull Object o) {
        return super.add(o);
    }

    /** {@link java.util.TreeSet#remove(Object)} */
    @Override
    public boolean remove(@NotNull Object o) {
        return super.remove(o);
    }
}
