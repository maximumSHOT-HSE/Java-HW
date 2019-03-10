package ru.hse.surkov;

import java.sql.Ref;

class A {
    static {
        System.out.println("Hello");
    }
}

/**
 * Class for creating correct (can be compiled) .java file by Class object.
 * Also, this class can compare two classes implementations by their class objects
 * and print differences between them.
 * */
public class Reflector {

    /**
     * Method creates new correct (valid) compilable .java file with
     * code of someClass.
     * In the created file all fields, methods, inner and nested classes and interfaces
     * will be declared with modifiers equivalent to the original class.
     * Returned types of all methods will be the same as the originals.
     * Generic methods and inner classes will save their generic entities.
     * */
    public static void printStructure(Class<?> someClass) {

    }

    public static void main(String[] args) throws ClassNotFoundException {
        ComplicatedClass<Integer, Character, Short, Integer, Reflector> x = new ComplicatedClass<>();
        Reflector.printStructure(x.getClass());
    }

    /**
     * Method receives two classes to be compared and prints into standard
     * output stream (console) all the different fields and methods.
     * */
    public static void diffClasses(Class<?> leftClass, Class<?> rightClass) {

    }
}
