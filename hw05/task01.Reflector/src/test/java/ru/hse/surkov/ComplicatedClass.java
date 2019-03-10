package ru.hse.surkov;

import java.util.*;
import java.util.function.Consumer;

interface MyInterfaceA {
    void methodA();
}

interface MyInterfaceB {
    void methodB();
}

interface MyInterfaceC {
    void methodC();
}

class MyBaseClass implements MyInterfaceB {

    @Override
    public void methodB() {

    }
}

public class ComplicatedClass <K, R extends Object, S, T extends Comparable<K>, U> extends MyBaseClass implements MyInterfaceA, MyInterfaceB, MyInterfaceC {

    public static int CONST = 10;
    private static Integer INTEGER_CONST = 1010101010;

    private String privateString = "private string";
    public Integer publicInteger = 420010;
    protected Map<? super K,  ? extends T> strangeMap = new TreeMap<>();
    private int primitiveInt = 2103;
    protected double primitiveDouble = 3.1410;
    public char symbol = 'x';
    private short primitiveShort = 210;
    public byte someByte = 10;
    public  boolean flagTrue = true;
    protected boolean flagFalse = false;
    String someString = "ten";

    public Set<? extends Comparable<Character>> set = new TreeSet<>();
    protected List<? extends Object> list = new LinkedList<>();

    @Override
    public void methodA() {

    }

    @Override
    public void methodB() {

    }

    @Override
    public void methodC() {

    }

    public int publicMethod() {
        return 0;
    }

    protected List protectedMethod() {
        return new LinkedList();
    }

    private <U extends K> Comparator<U> generciMethod() {
        return (o1, o0) -> 0; // lambda
    }

    public static class NestedClass {
        private int x = 10;
        private int y = 10;
        public String openField;
    }

    class InnerPackageLevelClass {
        protected String name = "TEN";
    }

    public void voidMethod() {
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
