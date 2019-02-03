package ru.hse.surkov.hw03;

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
    public Iterator descendingIterator() {
        return null;
    }

    /** {@link java.util.TreeSet#descendingSet()} */
    @Override
    public MyTreeSet descendingSet() {
        return null;
    }

    /** {@link java.util.TreeSet#first()} */
    @Override
    public Object first() {
        return null;
    }

    /** {@link java.util.TreeSet#last()} */
    @Override
    public Object last() {
        return null;
    }

    /** {@link java.util.TreeSet#lower(Object)} */
    @Override
    public Object lower(Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#floor(Object)} */
    @Override
    public Object floor(Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#ceiling(Object)} */
    @Override
    public Object ceiling(Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#higher(Object)} */
    @Override
    public Object higher(Object o) {
        return null;
    }

    /** {@link java.util.TreeSet#contains(Object)} */
    @Override
    public boolean contains(Object o) {
        return super.contains(o);
//        return false;
    }

    /** {@link java.util.TreeSet#add(Object)} */
    @Override
    public boolean add(Object o) {
        return super.add(o);
    }

    /** {@link java.util.TreeSet#remove(Object)} */
    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }
}
