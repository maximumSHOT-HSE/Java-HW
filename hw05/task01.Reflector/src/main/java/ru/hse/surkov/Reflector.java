package ru.hse.surkov;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

class A  {
    static {
        System.out.println("Hello");
    }

    protected final class finalInnerAClasss {

    }
}

final class finalClass extends A {

}

class A1 {
    int x;
    void f(Comparable<? extends Integer> c) {

    }
}

class B1 {
    int y;
    void f(Comparable<?> c) {

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
    public static void printStructure(@NotNull Class<?> someClass) {

    }

    private static String generateCode(@NotNull Class<?> someClass, Set<String> packages) {
        StringBuilder someClassCode = new StringBuilder();

        // declaration
        someClassCode.append(
            getClassDeclaration(someClass, packages)
        );

        // all fields
        someClassCode.append(
            getAllfields(someClass, packages)
        );

        someClassCode.append("\n");

        // all methods
        someClassCode.append(
            getAllMethods(someClass, packages)
        );

        // constructors
        someClassCode.append(
            getAllConstructors(someClass, packages)
        );

        // inner/nested classes/interfaces
        someClassCode.append(
            getAllClasses(someClass, packages)
        );

        // ending parenthesis
        someClassCode.append("}\n");

        return someClassCode.toString();
    }

    private static String getClassDeclaration(Class<?> someClass, final Set<String> packages) {
        return getDeclarationModifiers(someClass.getModifiers()) +
                (someClass.isInterface() ? "" : "class ") +
                someClass.getSimpleName() + " " +
                getFullGenericArguments(someClass.getTypeParameters()) +
                getExtensionString(someClass, packages) +
                getInterfacesString(someClass, packages) +
                "{\n";
    }

    private static String getAllClasses(Class<?> someClass, Set<String> packages) {
        StringBuilder classes = new StringBuilder();
        for (var clazz : someClass.getDeclaredClasses()) {
            classes.append(Reflector.generateCode(clazz, packages)).append("\n");
        }
        return classes.toString();
    }

    private static String getParametersDescribing(Type[] parameters, final Set<String> packages) {
        final Counter counter = new Counter();
        return Arrays.stream(parameters)
                .map(p -> {
                    counter.increment();
//                    packages.add(p.getClass().getPackageName());
                    return p.getTypeName() + " a" + counter.getCounter();
                })
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private static String getAllConstructors(Class<?> someClass, final Set<String> packages) {
        StringBuilder constructors = new StringBuilder();

        for (var constructor : someClass.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            constructors.append(Modifier.toString(constructor.getModifiers())).append(" ") // modifiers
                    .append(someClass.getSimpleName()); // name
            constructors.append(getParametersDescribing(constructor.getGenericParameterTypes(), packages)).append(" ")
                    .append(getExceptionsThrowableFromMethods(constructor.getExceptionTypes(), packages)) // exceptions
                    .append(" {\n").append("}\n\n"); // parameters
        }

        return constructors.toString();
    }

    private static String getMethodDeclaration(Method method, final Set<String> packages) {
        StringBuilder methodDeclaration = new StringBuilder();
        methodDeclaration.append(getDeclarationModifiers(method.getModifiers())); // modifiers
        methodDeclaration.append(getFullGenericArguments(method.getTypeParameters())).append(" "); // generic args of returned type
        methodDeclaration.append(method.getGenericReturnType().getTypeName()).append(" "); // return type
        methodDeclaration.append(method.getName()); // method name
        methodDeclaration.append(getParametersDescribing(method.getGenericParameterTypes(), packages)).append(" "); // arguments
        methodDeclaration.append(getExceptionsThrowableFromMethods(method.getExceptionTypes(), packages)); // exceptions
        return methodDeclaration.toString();
    }

    private static String getAllMethods(Class<?> someClass, final Set<String> packages) {
        StringBuilder methods = new StringBuilder();
        for (var method : someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            methods.append(getMethodDeclaration(method, packages));
            if (someClass.isInterface()) {
                methods.append(";\n");
            } else {
                methods.append(" {\n");
                if (!void.class.equals(method.getReturnType())) {
                    if (method.getReturnType().isPrimitive()) {
                        methods.append("return 0;\n");
                    } else {
                        methods.append("return null;\n");
                    }
                }
                methods.append("}\n\n");
            }
        }
        return methods.toString();
    }

    private static String getExceptionsThrowableFromMethods(Class<?>[] exceptions, final Set<String> packages) {
        if (exceptions.length == 0) {
            return "";
        }
        return Arrays.stream(exceptions)
                .map(p -> {
                    packages.add(p.getCanonicalName());
                    return p.getCanonicalName();
                })
                .collect(
                    Collectors.joining(
                        ", ", "throws ", " "
                    )
                );
    }

    private static String getFieldDeclaration(Field field) {
        StringBuilder fieldDeclaration = new StringBuilder();
        fieldDeclaration.append(getDeclarationModifiers(field.getModifiers())); // modifiers
        fieldDeclaration.append(field.getGenericType().getTypeName()).append(" ");
        fieldDeclaration.append(field.getName());
        return fieldDeclaration.toString();
    }

    private static String getAllfields(Class<?> someClass, final Set<String> packages) {
        StringBuilder fields = new StringBuilder();
        for (var field : someClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            packages.add(field.getClass().getCanonicalName());
            fields.append(getFieldDeclaration(field));
            if (Modifier.isFinal(field.getModifiers())) {
                if (field.getType().isPrimitive()) {
                    if (boolean.class.equals(field.getType())) {
                        fields.append(" = false");
                    } else {
                        fields.append(" = 0");
                    }
                } else {
                    fields.append(" = null");
                }
            }
            fields.append(";\n");
        }
        return fields.toString();
    }

    private static String getDeclarationModifiers(int modifiersId) {
        String modifiers = Modifier.toString(modifiersId);
        return modifiers.length() == 0 ? ""  : modifiers + " ";
    }

    public static void main(String[] args) throws ClassNotFoundException {
//        ComplicatedClass<Object, Object, Object, Comparable<? super Object>, Comparable<?>> x = new ComplicatedClass<>();
//        Reflector.printStructure(x.getClass());
//        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
//        Reflector.printStructure(A.class);
//        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
//        Reflector.printStructure(finalClass.class);
//        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
//        Reflector.printStructure(A.finalInnerAClasss.class);

//        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
//        Reflector.printStructure(String.class);
//        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
//        Reflector.printStructure(ArrayList.class);
        diffClasses(A1.class, B1.class);
    }

    private static String getExtensionString(Class<?> someClass, Set<String> packages) {
        Class<?> superClass = someClass.getSuperclass();
        if (superClass != null && superClass.getSuperclass() != null) {
            packages.add(someClass.getSuperclass().getCanonicalName());
            return "extends " + superClass.getSimpleName() + " ";
        } else {
            return "";
        }
    }

    private static String getInterfacesString(Class<?> someClass, final Set<String> packages) {
        Class<?>[] interfaces = someClass.getInterfaces();
        StringBuilder interfacesString = new StringBuilder();
        if (interfaces.length == 0) {
            return "";
        }
        for (var receivedInterface : interfaces) {
            packages.add(receivedInterface.getCanonicalName());
            if (interfacesString.length() > 0) {
                interfacesString.append(", ");
            }
            interfacesString.append(receivedInterface.getSimpleName());
        }
        return "implements " + interfacesString.toString() + " ";
    }

    private static String getFullGenericArguments(TypeVariable<?>[] typesVariable) {
        if (typesVariable.length == 0) {
            return "";
        }
        StringBuilder arguments = new StringBuilder();
        for (var x : typesVariable) {
            if (arguments.length() > 0) {
                arguments.append(", ");
            }
            arguments.append(x.getTypeName());
            if (x.getBounds().length == 0) {
                continue;
            }
            for (var y : x.getBounds()) {
                arguments.append(" extends ");
                arguments.append(y.getTypeName());
            }
            System.out.println();
        }
        return "<" + arguments.toString() + "> ";
    }

    private static class Counter {
        private int count;
        public void increment() {
            count++;
        }
        public int getCounter() {
            return count;
        }
    }

    private static String getDifferencesBetwwenHashSets(HashSet<String> leftSet, HashSet<String> rightSet) {
        StringBuilder log = new StringBuilder();
        for (var x : leftSet) {
            if (!rightSet.contains(x)) {
                log.append("\t").append(x).append("\n");
            }
        }
        return log.toString();
    }

    /**
     * {@link Reflector#diffClasses(Class, Class)}
     * @return text of log as a String without printing anything into console
     * */
    public static String getDiffernces(@NotNull Class<?> leftClass, @NotNull Class<?> rightClass) {
        StringBuilder log = new StringBuilder();

        HashSet<String> leftFields = new HashSet<>();
        HashSet<String> rightFields = new HashSet<>();
        HashSet<String> leftMethods = new HashSet<>();
        HashSet<String> rightMethods = new HashSet<>();
        Set<String> helper = new TreeSet<>(); // helper -- plug for packages storing

        for (var field : leftClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            leftFields.add(getFieldDeclaration(field));
        }

        for (var field : rightClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            rightFields.add(getFieldDeclaration(field));
        }

        for (var method : leftClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            leftMethods.add(getMethodDeclaration(method, helper));
        }

        for (var method : rightClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            rightMethods.add(getMethodDeclaration(method, helper));
        }

        log.append("Fields:\n\n");

        log.append(leftClass.getSimpleName()).append("\n");
        log.append(getDifferencesBetwwenHashSets(leftFields, rightFields));
        log.append("\n").append(rightClass.getSimpleName()).append("\n");
        log.append(getDifferencesBetwwenHashSets(rightFields, leftFields));

        log.append("Methods:\n\n");

        log.append(leftClass.getSimpleName()).append("\n");
        log.append(getDifferencesBetwwenHashSets(leftMethods, rightMethods));
        log.append("\n").append(rightClass.getSimpleName()).append("\n");
        log.append(getDifferencesBetwwenHashSets(rightMethods, leftMethods));

        return log.toString();
    }

    /**
     * Method receives two classes to be compared and prints into standard
     * output stream (console) all the different fields and methods.
     * Format of logging:
     * 1. Fields --
     *      firstly, fields of leftClass, which does not exist in rightClass will be printed
     *      secondly, fields of rightClass, which does not exist in leftClass will be printed
     * 2. Methods -- format the same as the fields
     * */
    public static void diffClasses(@NotNull Class<?> leftClass, @NotNull Class<?> rightClass) {
        System.out.println(getDiffernces(leftClass, rightClass));
    }
}
