package ru.hse.surkov.hw03;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Random;

/**
 * Simple binary search tree of keys
 * and heap of random priorities
 * */
public class RandomNode<E> extends Node<E> {

    private static final Random GENERATOR = new Random(153);

    protected long priority;

    RandomNode(@NotNull E value) {
        super(value);
        priority = GENERATOR.nextLong();
    }

    /**
     * Splits treap into two parts:
     * part with keys less than separator and
     * part with keys greater or equal then separator
     * without copying.
     * (<)(>=)
     * @return Pair of parts
     * */
    @NotNull public Pair<RandomNode<E>, RandomNode<E>> split(@NotNull E separator, @NotNull Comparator<? super E> comparator) {
        if (this == NULL_NODE) {
            return new Pair<>((RandomNode<E>) NULL_NODE, (RandomNode<E>) NULL_NODE);
        }

        Pair<RandomNode<E>, RandomNode<E>> splitted;
        if (comparator.compare(value, separator) < 0) {
            splitted = ((RandomNode<E>) right).split(separator, comparator);
            right = splitted.first;
            splitted.first = this;
        } else {
            splitted = ((RandomNode<E>) left).split(separator, comparator);
            left = splitted.second;
            splitted.second = this;
        }
        splitted.first.updateNode(true);
        splitted.second.updateNode(true);
        return splitted;
    }

    /**
     * Merges two treaps into one without copying.
     * If not all keys in left treap are less then all keys in right treap,
     * then result of merge will be undefined.
     * @return tree obtained by merging
     * */
    @NotNull public static RandomNode merge(@NotNull RandomNode fromLeft, @NotNull RandomNode fromRight) {
        RandomNode merged;
        if (fromLeft == NULL_NODE || fromRight == NULL_NODE) {
            merged = fromLeft == NULL_NODE ? fromRight : fromLeft;
        } else {
            if (fromLeft.priority < fromRight.priority) {
                merged = merge((RandomNode) fromLeft.right, fromRight);
                fromLeft.right = merged;
                merged = fromLeft;
            } else {
                merged = merge(fromLeft, (RandomNode) fromRight.left);
                fromRight.left = merged;
                merged = fromRight;
            }
        }
        merged.updateNode(true);
        return merged;
    }

    /**
     * Removes node by 'pointer', merges it's sons without copying.
     * @return new tree root
     * */
    @NotNull public RandomNode<E> removeNode() {
        var merged = (RandomNode<E>) merge((RandomNode<E>) left, (RandomNode<E>) right);
        if (parent == NULL_NODE) {
            return merged;
        }
        if (isLeftSon()) {
            parent.left = merged;
        } else if (isRightSon()) {
            parent.right = merged;
        }
        RandomNode<E> visitor = this;
        while (visitor.parent != NULL_NODE) {
            visitor.parent.updateNode(false);
            visitor = (RandomNode<E>) visitor.parent;
        }
        return visitor;
    }

    /**
     * Removes element o if such exists and returns new root.
     * If there was not such element null will be returned.
     * @return new tree root
     * */
    @NotNull public RandomNode<E> remove(@NotNull E o, @NotNull Comparator<? super E> comparator) {
        int order = comparator.compare(value, o);
        if (order == 0) {
            return removeNode();
        }
        var nodeTo = (RandomNode<E>) (order < 0 ? right : left);
        return nodeTo.remove(o, comparator);
    }
}
