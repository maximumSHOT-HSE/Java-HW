package hse.surkov.hw09;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyJUnit {

    private static List<TestData> executeTests(Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        var methods = clazz.getDeclaredMethods();
        List<Method> testMethods = new ArrayList<>();
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        for (var method : methods) {
            if (method.getAnnotation(Test.class) != null) {
                testMethods.add(method);
            }
            if (method.getAnnotation(Before.class) != null) {
                beforeMethods.add(method);
            }
            if (method.getAnnotation(After.class) != null) {
                afterMethods.add(method);
            }
            if (method.getAnnotation(BeforeClass.class) != null) {
                method.invoke(clazz);
            }
        }
        for (var testMethod : testMethods) {
            for (var beforeMethod : beforeMethods) {
                beforeMethod.invoke(clazz);
            }
            testMethod.invoke(clazz);
            for (var afterMethod : afterMethods) {
                afterMethod.invoke(clazz);
            }
        }
        for (var method : methods) {
            if (method.getAnnotation(AfterClass.class) != null) {
                method.invoke(clazz);
            }
        }
    }

    private static void printStatistics(List<TestData> testsData) {
        System.out.println("Total amount of tests: " + testsData.size());
        for (var testData : testsData) {
            testData.printStatistics();
        }
    }

    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("Please, enter class name: ");
        var className = scanner.nextLine();
        Class<?> loadedClass = null;
        try {
            loadedClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Sorry, but class not found.");
            System.exit(1);
        }
        List<TestData> statistics = null;
        try {
            statistics = executeTests(loadedClass);
        } catch (InvocationTargetException e) {
            System.out.println("Test method invocation error");
            System.exit(2);
        } catch (IllegalAccessException e) {
            System.out.println("Illegal access failure");
            System.exit(3);
        }
        printStatistics(statistics);
    }

    private static class TestData {

        private ResultType resultType = ResultType.NOT_EXECUTED;
        private String ignoreMessageReason;
        private String name;

        public void printStatistics() {
            System.out.print(name + ": " + resultType.toString());
            if (resultType.equals(ResultType.IGNORED)) {
                System.out.println("Reason: " + ignoreMessageReason);
            }
        }

        public enum ResultType {
            NOT_EXECUTED,
            IGNORED,
            PASSED,
            FAILED
        }
    }
}
