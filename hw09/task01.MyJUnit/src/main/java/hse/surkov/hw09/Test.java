package hse.surkov.hw09;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for test methods
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {

    /**
     * Indicates whether some exception should be thrown
     * from test method or not.
     * */
    boolean isExceptionExpected() default false;

    /**
     * Expected exception, which should be
     * thrown from test method.
     */
    Class<? extends Throwable> expectedException() default Exception.class;

    /**
     * Allows to set method as ignored for
     * testing system, hence method annotated
     * with this parameter will be skipped
     * during testing process.
     */
    boolean ignore() default false;

    /**
     * Reason of ignoring test method.
     */
    String ignoreReason() default "";
}
