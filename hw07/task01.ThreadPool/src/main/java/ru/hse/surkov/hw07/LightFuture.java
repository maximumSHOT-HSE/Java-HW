package ru.hse.surkov.hw07;

import java.util.function.Function;

/**
 * Abstraction for tasks to be calculated.
 * */
public interface LightFuture<T> {

    /**
     * Defines whether task is done or not.
     *
     * @return true if task is done and false otherwise
     * */
    boolean isReady();

    /**
     * Calculates and returns result of task execution.
     * If result has not been calculated yet then method
     * waits the moment when result will be available and returns it.
     *
     * @return result of executing task
     * @throws LightExecutionException if calculation of result has finished
     * with Exception
     * */
    T get() throws LightExecutionException;

    /**
     * Uses given function, which takes a result of source task and convert it
     * to another LightFuture object (convert to new task).
     *
     * @param function, which should be applied to result of given and
     * result of such composition should be returned. This function
     * takes a result of source task and convert it to another LightFuture
     * object (convert to new task)
     * */
    <U> LightFuture<U> thenApply(Function<? super T, U> function);
}
