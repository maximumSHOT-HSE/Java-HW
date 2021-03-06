package hse.surkov.hw09.TestClasses;

import hse.surkov.hw09.*;

import java.util.HashSet;
import java.util.Set;

public class BasicsTestClassTest {
    private static int beforeCounter = 0;
    private static int afterCounter = 0;
    private static Set<Integer> testSet = new HashSet<>();
    private static Set<Integer> beforeSet = new HashSet<>();
    private static Set<Integer> afterSet = new HashSet<>();

    @BeforeClass
    public static void beforeClass() {
        beforeCounter++;
    }

    @Before
    public void before() {
        beforeSet.add(testSet.size());
    }

    @Test
    public void test1() {
        testSet.add(1);
    }

    @Test
    public void test2() {
        testSet.add(2);
    }

    @After
    public void after() {
        afterSet.add(testSet.size());
    }

    @AfterClass
    public static void afterClass() {
        afterCounter++;
    }

    public static int getBeforeCounter() {
        return beforeCounter;
    }

    public static int getAfterCounter() {
        return afterCounter;
    }

    public static Set<Integer> getTestSet() {
        return testSet;
    }

    public static Set<Integer> getBeforeSet() {
        return beforeSet;
    }

    public static Set<Integer> getAfterSet() {
        return afterSet;
    }

    public static void clear() {
        beforeCounter = 0;
        afterCounter = 0;
        testSet.clear();
        beforeSet.clear();
        afterSet.clear();
    }
}
