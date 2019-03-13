package ru.hse.surkov.hw03;

import org.jetbrains.annotations.Nullable;

public final class Pair<F, S> {
    @Nullable private F first;
    @Nullable private S second;

    public Pair(@Nullable F first, @Nullable S second) {
        this.first = first;
        this.second = second;
    }

    @Nullable public F getFirst() {
        return first;
    }

    public void setFirst(@Nullable F first) {
        this.first = first;
    }

    @Nullable public S getSecond() {
        return second;
    }

    public void setSecond(@Nullable S second) {
        this.second = second;
    }
}
