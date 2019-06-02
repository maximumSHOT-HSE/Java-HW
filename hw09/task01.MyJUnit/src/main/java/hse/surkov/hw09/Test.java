package hse.surkov.hw09;

/**
 * Annotation for test methods
 * */
public @interface Test {

    /**
     * Expected exception, which should be
     * thrown from test method.
     * */
    Class<? extends Throwable> expected();

    /**
     * Allows to set method as ignored for
     * testing system, hence method annotated
     * with this parameter will be skipped
     * during testing process.
     * */
    String ignore();
}
