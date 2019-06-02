package hse.surkov.hw09;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MyJUnitTest {

    @RepeatedTest(10)
    void testBasics() throws InvocationTargetException, IllegalAccessException {
        BasicsTestClass.clear();
        MyJUnit.executeTests(BasicsTestClass.class);
        assertEquals(1, BasicsTestClass.getBeforeCounter());
        assertEquals(1, BasicsTestClass.getAfterCounter());
        assertEquals(2, BasicsTestClass.getTestSet().size());
        assertEquals(2, BasicsTestClass.getAfterSet().size());
        assertEquals(2, BasicsTestClass.getBeforeSet().size());
    }

    @Test
    void testExceptionHandling() throws InvocationTargetException, IllegalAccessException {
        var statistics = MyJUnit.executeTests(ExceptionClassTest.class);
        for (var testData : statistics) {
            switch (testData.getName()) {
                case "success":
                    assertEquals(MyJUnit.TestData.ResultType.PASSED, testData.getResultType());
                    break;
                case "successThrow":
                    assertEquals(MyJUnit.TestData.ResultType.PASSED, testData.getResultType());
                    break;
                case "nothingWasThrown":
                    assertEquals(MyJUnit.TestData.ResultType.FAILED, testData.getResultType());
                    break;
                case "otherExceptionWasThrown":
                    assertEquals(MyJUnit.TestData.ResultType.FAILED, testData.getResultType());
                    break;
                default:
                    fail();
            }
        }
    }

    @Test
    void testIgnoreHandling() throws InvocationTargetException, IllegalAccessException {
        var statistics = MyJUnit.executeTests(IgnoreClassTest.class);
        for (var testData : statistics) {
            switch (testData.getName()) {
                case "noIgnore":
                    assertEquals(MyJUnit.TestData.ResultType.PASSED, testData.getResultType());
                    break;
                case "noIgnoreWithException":
                    assertEquals(MyJUnit.TestData.ResultType.FAILED, testData.getResultType());
                    break;
                case "ignoreException":
                    assertEquals(MyJUnit.TestData.ResultType.IGNORED, testData.getResultType());
                    break;
                case "ignoreNothing":
                    assertEquals(MyJUnit.TestData.ResultType.IGNORED, testData.getResultType());
                    assertEquals("no reason", testData.getMessage());
                    break;
                default:
                    fail();
            }
        }
    }

    @Test
    void testComplicatedExceptionHandling() throws InvocationTargetException, IllegalAccessException {
        var statistics = MyJUnit.executeTests(ComplicatedExceptionClassTest.class);
        for (var testData : statistics) {
            switch (testData.getName()) {
                case "expcetedException":
                    assertEquals(MyJUnit.TestData.ResultType.PASSED, testData.getResultType());
                    break;
                case "unexpectedException":
                    assertEquals(MyJUnit.TestData.ResultType.FAILED, testData.getResultType());
                    break;
                case "ignoreExpectedException":
                    assertEquals(MyJUnit.TestData.ResultType.IGNORED, testData.getResultType());
                    break;
                case "ignoreUnexpectedException":
                    assertEquals(MyJUnit.TestData.ResultType.IGNORED, testData.getResultType());
                    break;
                default:
                    fail();
            }
        }
    }
}