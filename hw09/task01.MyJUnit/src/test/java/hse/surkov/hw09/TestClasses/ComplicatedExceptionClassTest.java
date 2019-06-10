package hse.surkov.hw09.TestClasses;

import hse.surkov.hw09.Test;

public class ComplicatedExceptionClassTest {

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    public void expcetedException() {
        throw new IllegalArgumentException();
    }

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    public void unexpectedException() {
        throw new IllegalStateException();
    }

    @Test(ignore = true, isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    public void ignoreExpectedException() {
        throw new IllegalArgumentException();
    }

    @Test(ignore = true, isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    public void ignoreUnexpectedException() {
        throw new IllegalStateException();
    }
}
