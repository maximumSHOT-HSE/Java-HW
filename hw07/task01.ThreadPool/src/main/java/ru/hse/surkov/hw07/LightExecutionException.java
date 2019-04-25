package ru.hse.surkov.hw07;

import org.jetbrains.annotations.Nullable;

public class LightExecutionException extends Exception {

    @Nullable Exception parentException;

    public LightExecutionException(Exception e) {
        this.parentException = e;
    }
}
