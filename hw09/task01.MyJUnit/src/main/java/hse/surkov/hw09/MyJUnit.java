package hse.surkov.hw09;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MyJUnit {

    private static final int CLASS_NOT_FOUND_ERROR = 1;
    private static final int INVOCATION_ERROR = 2;
    private static final int ACCESS_ERROR = 3;
    private static final int FAILURE = 4;

    @Nullable private static Object newInstanceWithDefaultConstructor(@NotNull Class<?> clazz) throws
            InvocationTargetException, IllegalAccessException {
        for (var constructor : clazz.getConstructors()) {
            try {
                return constructor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @NotNull private static List<Method> retrieveMethodsByAnnotation(
            @NotNull Method[] methods,
            @NotNull Class<? extends Annotation> annotationClass) {
        return Arrays.stream(methods)
                .filter(method -> method.getAnnotation(annotationClass) != null)
                .collect(Collectors.toList());
    }

    /**
     * Executes tests for given loaded class and returns
     * statistics about it.
     * */
    @NotNull public static List<TestData> executeTests(@NotNull Class<?> clazz) throws
            InvocationTargetException, IllegalAccessException {
        var methods = clazz.getDeclaredMethods();
        var testMethods = retrieveMethodsByAnnotation(methods, Test.class);
        var beforeMethods = retrieveMethodsByAnnotation(methods, Before.class);
        var afterMethods = retrieveMethodsByAnnotation(methods, After.class);
        var beforeClassMethods = retrieveMethodsByAnnotation(methods, BeforeClass.class);
        var afterClassMethods = retrieveMethodsByAnnotation(methods, AfterClass.class);
        var instance = newInstanceWithDefaultConstructor(clazz);
        if (instance == null) {
            throw new IllegalArgumentException("Default construct not found");
        }
        for (var method : beforeClassMethods) {
            method.invoke(instance);
        }
        var statistics = new ArrayList<TestData>();
        for (var testMethod : testMethods) {
            for (var beforeMethod : beforeMethods) {
                beforeMethod.invoke(instance);
            }
            var annotation = testMethod.getAnnotation(Test.class);
            var testData = new TestData(TestData.ResultType.NOT_EXECUTED, testMethod.getName());
            if (annotation.ignore()) {
                testData.setResultType(TestData.ResultType.IGNORED);
                testData.setMessage(annotation.ignoreReason());
            } else {
                try {
                    testMethod.invoke(instance);
                    if (annotation.isExceptionExpected()) {
                        testData.setResultType(TestData.ResultType.FAILED);
                        testData.setMessage(annotation.expectedException().getName()
                                + " expected to be thrown, but nothing was thrown");
                    } else {
                        testData.setResultType(TestData.ResultType.PASSED);
                    }
                } catch (InvocationTargetException catched) {
                    var e = catched.getCause();
                    if (annotation.isExceptionExpected() &&
                            annotation.expectedException().isInstance(e)) {
                        testData.setResultType(TestData.ResultType.PASSED);
                    } else {
                        testData.setResultType(TestData.ResultType.FAILED);
                        testData.setMessage("Unexpected exception "
                            + e.getClass().getName() + " was thrown, message: "
                            + e.getMessage());
                    }
                }
            }
            statistics.add(testData);
            for (var afterMethod : afterMethods) {
                afterMethod.invoke(instance);
            }
        }
        for (var method : afterClassMethods) {
            method.invoke(instance);
        }
        return statistics;
    }

    private static void printStatistics(@NotNull List<TestData> testsData) {
        System.out.println("Total amount of tests: " + testsData.size());
        for (var testData : testsData) {
            testData.printStatistics();
        }
    }

    public static void main(@NotNull String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("Please, enter class name: ");
        var className = scanner.nextLine();
        Class<?> loadedClass = null;
        try {
            loadedClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Sorry, but class not found.");
            System.exit(CLASS_NOT_FOUND_ERROR);
        }
        List<TestData> statistics = null;
        try {
            statistics = executeTests(loadedClass);
        } catch (InvocationTargetException e) {
            System.out.println("Test method invocation error");
            e.printStackTrace();
            System.exit(INVOCATION_ERROR);
        } catch (IllegalAccessException e) {
            System.out.println("Illegal access failure");
            e.printStackTrace();
            System.exit(ACCESS_ERROR);
        } catch (Exception e) {
            System.out.println("Failure");
            e.printStackTrace();
            System.exit(FAILURE);
        }
        printStatistics(statistics);
    }

    /**
     * Data holder for test, which stores
     * result of execution of particular tests,
     * its name and message associated with
     * execution test.
     */
    public static class TestData {

        @NotNull private ResultType resultType;
        @NotNull private String message = "";
        @NotNull private String name;

        public TestData(@NotNull ResultType resultType, @NotNull String name) {
            this.resultType = resultType;
            this.name = name;
        }

        public void setResultType(@NotNull ResultType resultType) {
            this.resultType = resultType;
        }

        public void setMessage(@NotNull String message) {
            this.message = message;
        }

        public void printStatistics() {
            System.out.println(name + ": " + resultType.toString());
            if (!resultType.equals(ResultType.PASSED)) {
                System.out.println("Message: " + message);
            }
        }

        public enum ResultType {
            NOT_EXECUTED,
            IGNORED,
            PASSED,
            FAILED
        }

        @NotNull public ResultType getResultType() {
            return resultType;
        }

        @NotNull public String getMessage() {
            return message;
        }

        @NotNull public String getName() {
            return name;
        }
    }
}
