package hse.surkov.hw09;

public class ExceptionClassTest {

    @Test
    void success() {

    }

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    void nothingWasThrown() {

    }

    @Test(isExceptionExpected = true, expectedException = IllegalArgumentException.class)
    void otherExceptionWasThrown() {
        throw new IllegalStateException();
    }

    @Test(isExceptionExpected = true, expectedException = RuntimeException.class)
    void successThrow() {
        throw new RuntimeException();
    }
}
