package hse.surkov.hw09;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for methods, which
 * should be executed before each test
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}
