package hse.surkov.hw09;

public class IgnoreClassTest {

    private static int counter = 0;

    @Test
    void noIgnore() {

    }

    @Test
    void noIgnoreWithException() {
        throw new IllegalArgumentException();
    }

    @Test(ignore = true)
    void ignoreException() {
        throw new IllegalStateException();
    }

    @Test(ignore = true, ignoreReason = "no reason")
    void ignoreNothing() {
        counter++;
    }
}
