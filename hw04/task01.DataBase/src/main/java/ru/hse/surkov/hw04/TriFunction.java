package ru.hse.surkov.hw04;

/**
 * Represents a function that accepts three arguments and produces a result.
 *
 * @param <A1> the type of the first argument to the function
 * @param <A2> the type of the second argument to the function
 * @param <A3> the type of the third argument to the function
 * @param <R> the type of the result of the function
 */
@FunctionalInterface
public interface TriFunction<A1, A2, A3, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param a1 the first function argument
     * @param a2 the second function argument
     * @param a3 the third function argument
     * @return the function result
     */
    R apply(A1 a1, A2 a2, A3 a3);
}
