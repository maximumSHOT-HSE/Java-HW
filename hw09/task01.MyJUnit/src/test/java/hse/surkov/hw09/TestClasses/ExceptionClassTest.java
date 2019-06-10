package hse.surkov.hw09.TestClasses;

import hse.surkov.hw09.Test;

public class ExceptionClassTest {

    @Test
    public void success() {

    }

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    public void nothingWasThrown() {

    }

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    public void otherExceptionWasThrown() {
        throw new IllegalStateException();
    }

    @Test(isExceptionExpected = true, expectedException = RuntimeException.class)
    public void successThrow() {
        throw new RuntimeException();
    }
}
