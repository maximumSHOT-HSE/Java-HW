package ru.hse.surkov;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
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
        System.out.println(generateCode(someClass, 0));
    }

    private static String generateCode(@NotNull Class<?> someClass, int depth) {
        StringBuilder someClassCode = new StringBuilder();

        someClassCode.append("\t".repeat(depth));
        // declaration
        someClassCode.append(
                getDeclarationModifiers(someClass.getModifiers()) + "class " +
                        someClass.getSimpleName() + " " +
                        getFullGenericArguments(someClass.getTypeParameters()) +
                        getExtensionString(someClass) +
                        getInterfacesString(someClass) +
                        "{\n"
        );

        // all fields
        someClassCode.append(
            getAllfields(someClass, depth + 1)
        );

        someClassCode.append("\n");

        // all methods
        someClassCode.append(
            getAllMethods(someClass, depth + 1)
        );

        // constructors
        someClassCode.append(
            getAllConstructors(someClass, depth + 1)
        );

        someClassCode.append("\t".repeat(depth));
        // ending parenthesis
        someClassCode.append("}\n");

        return someClassCode.toString();
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

    private static String getAllConstructors(Class<?> someClass, int depth) {
        StringBuilder constructors = new StringBuilder();

        for (var constructor : someClass.getDeclaredConstructors()) {
            constructors.append("\t".repeat(depth));
            constructors.append(Modifier.toString(constructor.getModifiers())).append(" ").append(someClass.getSimpleName());
            constructors.append(getParametersDescribing(constructor.getGenericParameterTypes())).append(" {\n\t".repeat(depth)).append("}\n\n");
        }

        return constructors.toString();
    }

    private static String getAllMethods(Class<?> someClass, int depth) {
        StringBuilder methods = new StringBuilder();
        for (var method : someClass.getDeclaredMethods()) {
            methods.append("\t".repeat(depth)); // tabs
            methods.append(getDeclarationModifiers(method.getModifiers())); // modifiers
            methods.append(getFullGenericArguments(method.getTypeParameters())).append(" "); // generic args of returned type
            methods.append(method.getGenericReturnType().getTypeName()).append(" "); // return type
            methods.append(method.getName()); // method name
            methods.append(getParametersDescribing(method.getGenericParameterTypes())).append(" "); // arguments
            methods.append(getExceptionsThrowableFromMethod(method)).append(" {\n");
            if(!void.class.equals(method.getReturnType())) {
                if (method.getReturnType().isPrimitive()) {
                    methods.append("\t".repeat(depth + 1)).append("return 0;\n");
                } else {
                    methods.append("\t".repeat(depth + 1)).append("return null;\n");
                }
            }
            methods.append("\t".repeat(depth)).append("}\n\n");
        }
        return methods.toString();
    }

    private static String getExceptionsThrowableFromMethod (Method method) {
        if (method.getExceptionTypes().length == 0) {
            return "";
        }
        return Arrays.stream(method.getExceptionTypes())
                .map(e -> e.getTypeName())
                .collect(
                    Collectors.joining(
                        ", ", "throws ", " "
                    )
                );
    }

    private static String getAllfields(Class<?> someClass, int depth) {
        StringBuilder fields = new StringBuilder();
        for (var field : someClass.getDeclaredFields()) {
            fields.append("\t".repeat(depth));
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

    /**
     * Method receives two classes to be compared and prints into standard
     * output stream (console) all the different fields and methods.
     * */
    public static void diffClasses(@NotNull Class<?> leftClass, @NotNull Class<?> rightClass) {

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
}
