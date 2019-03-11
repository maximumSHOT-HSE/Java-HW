package ru.hse.surkov;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.*;
import java.util.Arrays;
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
        System.out.println(generateCode(someClass));
    }

    private static String generateCode(@NotNull Class<?> someClass) {
        StringBuilder someClassCode = new StringBuilder();

        // declaration
        someClassCode.append(
            getClassDeclaration(someClass)
        );

        // all fields
        someClassCode.append(
            getAllfields(someClass)
        );

        someClassCode.append("\n");

        // all methods
        someClassCode.append(
            getAllMethods(someClass)
        );

        // constructors
        someClassCode.append(
            getAllConstructors(someClass)
        );

        // inner/nested classes/interfaces
        someClassCode.append(
            getAllClasses(someClass)
        );

        // ending parenthesis
        someClassCode.append("}\n");

        return someClassCode.toString();
    }

    private static String getClassDeclaration(Class<?> someClass) {
        return getDeclarationModifiers(someClass.getModifiers()) +
                (someClass.isInterface() ? "" : "class ") +
                someClass.getSimpleName() + " " +
                getFullGenericArguments(someClass.getTypeParameters()) +
                getExtensionString(someClass) +
                getInterfacesString(someClass) +
                "{\n";
    }

    private static String getAllClasses(Class<?> someClass) {
        StringBuilder classes = new StringBuilder();
        for (var clazz : someClass.getDeclaredClasses()) {
            classes.append(Reflector.generateCode(clazz)).append("\n");
        }
        return classes.toString();
    }

    private static String getParametersDescribing(Type[] parameters) {
        final Counter counter = new Counter();
        return Arrays.stream(parameters)
                .map(p -> {
                    counter.increment();
                    return p.getTypeName() + " a" + counter.getCounter();
                })
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private static String getAllConstructors(Class<?> someClass) {
        StringBuilder constructors = new StringBuilder();

        for (var constructor : someClass.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            constructors.append(Modifier.toString(constructor.getModifiers())).append(" ") // modifiers
                    .append(someClass.getSimpleName()); // name
            constructors.append(getParametersDescribing(constructor.getGenericParameterTypes())).append(" ")
                    .append(getExceptionsThrowableFromMethods(constructor.getExceptionTypes())) // exceptions
                    .append(" {\n").append("}\n\n"); // parameters
        }

        return constructors.toString();
    }

    private static String getAllMethods(Class<?> someClass) {
        StringBuilder methods = new StringBuilder();
        for (var method : someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            methods.append(getDeclarationModifiers(method.getModifiers())); // modifiers
            methods.append(getFullGenericArguments(method.getTypeParameters())).append(" "); // generic args of returned type
            methods.append(method.getGenericReturnType().getTypeName()).append(" "); // return type
            methods.append(method.getName()); // method name
            methods.append(getParametersDescribing(method.getGenericParameterTypes())).append(" "); // arguments
            methods.append(getExceptionsThrowableFromMethods(method.getExceptionTypes()));
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

    private static String getExceptionsThrowableFromMethods(Class<?>[] exceptions) {
        if (exceptions.length == 0) {
            return "";
        }
        return Arrays.stream(exceptions)
                .map(Class::getCanonicalName)
                .collect(
                    Collectors.joining(
                        ", ", "throws ", " "
                    )
                );
    }

    private static String getAllfields(Class<?> someClass) {
        StringBuilder fields = new StringBuilder();
        for (var field : someClass.getDeclaredFields()) {
            if (field.isSynthetic()) {
                continue;
            }
            fields.append(getDeclarationModifiers(field.getModifiers())); // modifiers
            fields.append(field.getGenericType().getTypeName()).append(" ");
            fields.append(field.getName());
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
        ComplicatedClass<Object, Object, Object, Comparable<? super Object>, Comparable<?>> x = new ComplicatedClass<>();
        Reflector.printStructure(x.getClass());
        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
        Reflector.printStructure(A.class);
        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
        Reflector.printStructure(finalClass.class);
        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
        Reflector.printStructure(A.finalInnerAClasss.class);

//        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
//        Reflector.printStructure(String.class);
//        System.out.println("\n\n\n\n--------------NEXT TEST---------------\n\n\n");
//        Reflector.printStructure(ArrayList.class);
    }

    private static String getExtensionString(Class<?> someClass) {
        Class<?> superClass = someClass.getSuperclass();
        if (superClass != null && superClass.getSuperclass() != null) {
            return "extends " + superClass.getSimpleName() + " ";
        } else {
            return "";
        }
    }

    private static String getInterfacesString(Class<?> someClass) {
        Class<?>[] interfaces = someClass.getInterfaces();
        StringBuilder interfacesString = new StringBuilder();
        if (interfaces.length == 0) {
            return "";
        }
        for (var receivedInterface : interfaces) {
            if (interfacesString.length() > 0) {
                interfacesString.append(", ");
            }
            interfacesString.append(receivedInterface.getSimpleName());
        }
        return "implements " + interfacesString.toString() + " ";
    }

    private static String getIsFinalString(Class<?> someClass) {
        if (Modifier.isFinal(someClass.getModifiers())) {
            return "final ";
        } else {
            return "";
        }
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

    /**
     * Method receives two classes to be compared and prints into standard
     * output stream (console) all the different fields and methods.
     * */
    public static void diffClasses(@NotNull Class<?> leftClass, @NotNull Class<?> rightClass) {

    }
}
