package hse.surkov.hw09;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for test methods
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
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
