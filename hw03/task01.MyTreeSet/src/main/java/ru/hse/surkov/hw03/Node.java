package ru.hse.surkov.hw03;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

/**
 * Simple binary search tree presented as a set
 * of Nodes, which stores links to their
 * parent and their sons (two or less),
 * key value and subtree size
 * */
public class Node<E> {
    /*
    * Fictitious node, which can be used in many problems
    * without any troubles related with NullPointerException
    * (code size will be much more compact).
    * */
    @NotNull public static final Node NULL_NODE;

    static {
        NULL_NODE = new Node();
    }

    private Node() {
        left = this;
        right = this;
        parent = this;
    }

    @NotNull protected Node<E> left;
    @NotNull protected Node<E> right;
    @NotNull protected Node<E> parent;
    @NotNull protected E value; // Only NULL_NODE has Nullable value
    protected int subtreeSize; // including current Node

    Node(@NotNull E value) {
        left = NULL_NODE;
        right = NULL_NODE;
        parent = NULL_NODE;
        this.value = value;
        subtreeSize = 1;
    }

    public boolean isSon() {
        return parent != NULL_NODE;
    }

    public boolean isLeftSon() {
        return parent != NULL_NODE && (parent.left == this);
    }

    public boolean isRightSon() {
        return parent != NULL_NODE && (parent.right == this);
    }

    /**
     * Recalculates vertex fields
     * and optionally deletes link to the parent.
     * */
    public void updateNode(boolean deleteParent) {
        if (deleteParent) {
            parent = NULL_NODE;
        }
        subtreeSize = left.subtreeSize + right.subtreeSize;
    }

    @NotNull public Node<E> moveDeepLeft() {
        Node<E> visitor = this;
        while (visitor.left != NULL_NODE) {
            visitor = visitor.left;
        }
        return visitor;
    }

    @NotNull public Node<E> moveDeepRight() {
        Node<E> visitor = this;
        while (visitor.right != NULL_NODE) {
            visitor = visitor.right;
        }
        return visitor;
    }

    @NotNull public Node<E> getPrev() {
        if (left != NULL_NODE) {
            return left.moveDeepRight();
        }
        Node<E> visitor = this;
        while (!visitor.isRightSon()) {
            visitor = visitor.parent;
        }
        return visitor.parent;
    }

    @NotNull public Node<E> getNext() {
        if (right != NULL_NODE) {
            return right.moveDeepLeft();
        }
        Node<E> visitor = this;
        while (!visitor.isLeftSon()) {
            visitor = visitor.parent;
        }
        return visitor.parent;
    }

    private int getOrder(@NotNull E o, @NotNull Comparator<? super E> comparator) {
        return this == NULL_NODE ? 0 : comparator.compare(value, o);
    }

    @NotNull public Node<E> floorNode(@NotNull E o, @NotNull Comparator<? super E> comparator) {
        int order = getOrder(o, comparator);
        if (order == 0) {
            return this;
        }
        if (order > 0) {
            return left.floorNode(o, comparator);
        }
        Node<E> candidate = right.floorNode(o, comparator);
        if (candidate == NULL_NODE) {
            candidate = this;
        }
        return candidate;
    }

    @NotNull public Node<E> ceilingNode(@NotNull E o, @NotNull Comparator<? super E> comparator) {
        int order = getOrder(o, comparator);
        if (order == 0) {
            return this;
        }
        if (order < 0) {
            return right.ceilingNode(o, comparator);
        }
        Node<E> candidate = left.ceilingNode(o, comparator);
        if (candidate == NULL_NODE) {
            candidate = this;
        }
        return candidate;
    }
}

