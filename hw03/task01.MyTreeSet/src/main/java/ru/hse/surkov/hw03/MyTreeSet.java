package ru.hse.surkov.hw03;

import java.util.Iterator;
import java.util.Set;

public interface MyTreeSet<E> extends Set<E> {

    /** {@link java.util.TreeSet#descendingIterator()} */
    Iterator<E> descendingIterator();

    /** {@link java.util.TreeSet#descendingSet()} */
    MyTreeSet<E> descendingSet();

    /** {@link java.util.TreeSet#first()} */
    E first();

    /** {@link java.util.TreeSet#last()} */
    E last();

    /** {@link java.util.TreeSet#lower(E)} */
    E lower(E e);

    /** {@link java.util.TreeSet#floor(E)} */
    E floor(E e);

    /** {@link java.util.TreeSet#ceiling(E)} */
    E ceiling(E e);

    /** {@link java.util.TreeSet#higher(E)} */
    E higher(E e);
}
