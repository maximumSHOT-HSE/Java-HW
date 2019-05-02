package ru.hse.surkov.hw07;

import org.jetbrains.annotations.Nullable;

/**
 * Exception, which stores cause of incorrect calculation
 * of task in thread pool.
 * */
public class LightExecutionException extends Exception {

    @Nullable private Exception parentException;

    public LightExecutionException(Exception e) {
        this.parentException = e;
    }
}
