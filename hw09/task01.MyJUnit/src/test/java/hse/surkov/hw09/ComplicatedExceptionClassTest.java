package hse.surkov.hw09;

public class ComplicatedExceptionClassTest {

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    void expcetedException() {
        throw new IllegalArgumentException();
    }

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    void unexpectedException() {
        throw new IllegalStateException();
    }

    @Test(ignore = true, isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    void ignoreExpectedException() {
        throw new IllegalArgumentException();
    }

    @Test(ignore = true, isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    void ignoreUnexpectedException() {
        throw new IllegalStateException();
    }
}
