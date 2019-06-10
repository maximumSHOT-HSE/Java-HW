package hse.surkov.hw09.TestClasses;

import hse.surkov.hw09.Test;

public class IgnoreClassTest {

    private static int counter = 0;

    @Test
    public void noIgnore() {

    }

    @Test
    public void noIgnoreWithException() {
        throw new IllegalArgumentException();
    }

    @Test(ignore = true)
    public void ignoreException() {
        throw new IllegalStateException();
    }

    @Test(ignore = true, ignoreReason = "no reason")
    public void ignoreNothing() {
        counter++;
    }
}
