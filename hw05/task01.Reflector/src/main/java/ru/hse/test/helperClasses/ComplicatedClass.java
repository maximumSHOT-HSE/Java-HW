package ru.hse.test.helperClasses;

import jdk.jshell.spi.ExecutionControl;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class ComplicatedClass <K, R extends Object, S, T extends Comparable<? super K>, U extends Comparable<? extends S>> extends MyBaseClass implements MyInterfaceA, MyInterfaceB, MyInterfaceC {

    public ComplicatedClass() {
    }

    private ComplicatedClass(K k) throws ExecutionControl.NotImplementedException {

    }

    protected ComplicatedClass(Integer value, TreeSet<? super  K> set) {
    }

    public final static char CONST_CHAR = 0;
    public final static int CONST = 10;
    public final static boolean FLAG = false;
    protected final static String STRING_CONST = "STRING CONST";
    private static Integer INTEGER_CONST = 1010101010;

    private String privateString = "private string";
    public Integer publicInteger = 420010;
    protected Map<? super K, ? extends T> strangeMap = new TreeMap<>();
    private int primitiveInt = 2103;
    protected double primitiveDouble = 3.1410;
    public char symbol = 'x';
    private short primitiveShort = 210;
    public byte someByte = 10;
    public  boolean flagTrue = true;
    protected boolean flagFalse = false;
    String someString = "ten";
    public int[] vars;
    protected Set<Integer> sets[];

    public Set<? extends Comparable<Character>> set = new TreeSet<>();
    protected List<? extends Object> list = new LinkedList<>();

    @Override
    public void methodA() {

    }

    @Override
    public void methodC() {

    }

    public int publicMethod(List<Set<Integer>> set, Integer count) {
        return 0;
    }

    public Integer twoArgumentsMethod(Character c, String s) throws RuntimeException, IOException {
        return 42;
    }

    public static void someFunction() {
        // TODO
    }

    protected List protectedMethod(Comparable<? extends  K> comparable) {
        return new LinkedList();
    }

    @Override
    public void methodB() {

    }

//    private <U extends K> Comparator<U> generciMethod() {
//        return (o1, o0) -> 0; // lambda
//    }

    public static class NestedClass {
        private int x = 10;
        private int y = 10;
        public String openField;

//        protected final class NestedInnerClass {
//            public void foo() {
//
//            }
//        }
    }

    class InnerPackageLevelClass {
        protected String name = "TEN";
    }


    public <W extends K> void voidMethod(W xxx) {
        new Iterator<>() { // anonymous class

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object next() {
                return null;
            }

            @Override
            public void remove() {

            }

            @Override
            public void forEachRemaining(Consumer<? super Object> action) {

            }
        };
    }

    private interface InnerInterface {
        void myAwesomeFunction();
    }

    protected static interface NestedInterface <K> {
        Comparator<K> oneMoreAwesomeInterface();
    }
}
