package hse.surkov.hw09;

import hse.surkov.hw09.TestClasses.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class MyJUnitTest {

    @RepeatedTest(10)
    void testBasics() throws InvocationTargetException, IllegalAccessException {
        BasicsTestClassTest.clear();
        MyJUnit.executeTests(BasicsTestClassTest.class);
        assertEquals(1, BasicsTestClassTest.getBeforeCounter());
        assertEquals(1, BasicsTestClassTest.getAfterCounter());
        assertEquals(2, BasicsTestClassTest.getTestSet().size());
        assertEquals(2, BasicsTestClassTest.getAfterSet().size());
        assertEquals(2, BasicsTestClassTest.getBeforeSet().size());
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

    @Test
    void testExecutionTime() throws InvocationTargetException, IllegalAccessException {
        var statistics = MyJUnit.executeTests(MeasureTimeClass.class);
        for (var testData : statistics) {
            if ("test".equals(testData.getName())) {
                assertTrue(testData.getExecutionTimeMilliseconds() >= 500);
            } else {
                fail();
            }
        }
    }

    @Test
    void testStaticBeforeClass() {
        assertThrows(IllegalStateException.class, () -> MyJUnit.executeTests(BeforeClassTest.class));
    }

    @Test
    void testStaticAfterClass() {
        assertThrows(IllegalStateException.class, () -> MyJUnit.executeTests(AfterClassTest.class));
    }
}