package ru.hse.surkov;

import jdk.jshell.spi.ExecutionControl;

class ComplicatedClass <K extends java.lang.Object, R extends java.lang.Object, S extends java.lang.Object, T extends java.lang.Comparable<? super K>, U extends java.lang.Comparable<? extends S>> extends MyBaseClass implements MyInterfaceA, MyInterfaceB, MyInterfaceC {
    public static final char CONST_CHAR = 0;
    public static final int CONST = 0;
    public static final boolean FLAG = false;
    protected static final java.lang.String STRING_CONST = null;
    private static java.lang.Integer INTEGER_CONST;
    private java.lang.String privateString;
    public java.lang.Integer publicInteger;
    protected java.util.Map<? super K, ? extends T> strangeMap;
    private int primitiveInt;
    protected double primitiveDouble;
    public char symbol;
    private short primitiveShort;
    public byte someByte;
    public boolean flagTrue;
    protected boolean flagFalse;
    java.lang.String someString;
    public int[] vars;
    protected java.util.Set<java.lang.Integer>[] sets;
    public java.util.Set<? extends java.lang.Comparable<java.lang.Character>> set;
    protected java.util.List<?> list;

    public  void methodA()  {
    }

    public  void methodC()  {
    }

    public  int publicMethod(java.util.List<java.util.Set<java.lang.Integer>> a1, java.lang.Integer a2)  {
        return 0;
    }

    public  java.lang.Integer twoArgumentsMethod(java.lang.Character a1, java.lang.String a2) throws java.lang.RuntimeException, java.io.IOException  {
        return null;
    }

    public static  void someFunction()  {
    }

    protected  java.util.List protectedMethod(java.lang.Comparable<? extends K> a1)  {
        return null;
    }

    public  void methodB()  {
    }

    public <W extends K>  void voidMethod(W a1)  {
    }

    private ComplicatedClass(K a1) throws jdk.jshell.spi.ExecutionControl.NotImplementedException  {
    }

    protected ComplicatedClass(java.lang.Integer a1, java.util.TreeSet<? super K> a2)  {
    }

    public ComplicatedClass()  {
    }

    protected abstract static interface NestedInterface <K extends java.lang.Object> {

        public abstract  java.util.Comparator<K> oneMoreAwesomeInterface() ;
    }

    private abstract static interface InnerInterface {

        public abstract  void myAwesomeFunction() ;
    }

    class InnerPackageLevelClass {
        protected java.lang.String name;

        InnerPackageLevelClass(ru.hse.surkov.ComplicatedClass a1)  {
        }

    }

    public static class NestedClass {
        private int x;
        private int y;
        public java.lang.String openField;

        public NestedClass()  {
        }

    }

}

public class ReflectorTest {
}
