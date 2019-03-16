package ru.hse.surkov;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Class for creating correct (can be compiled) .java file by Class object.
 * Also, this class can compare two classes implementations by their class objects
 * and print differences between them.
 * */
public class Reflector {

    /**
     * Method creates new correct (valid) compilable .java file with
     * code of someClass
     * (file will be created (or will be replaced) at the same place as
     * the directory where Reflector has been executed).
     * In the created file all fields, methods, inner and nested classes and interfaces
     * will be declared with modifiers equivalent to the original class.
     * Returned types of all methods will be the same as the originals.
     * Generic methods and inner classes will save their generic entities.
     * @throws IOException if there is some problems with creating .java file
     * (for instance, problems with permission)
     * */
    public static void printStructure(@NotNull Class<?> someClass) throws IOException, FormatterException {
        try (FileWriter fileWriter = new FileWriter(someClass.getSimpleName() + ".java")) {
            String generatedCode = getGeneratedCode(someClass);
            fileWriter.write(new Formatter().formatSource(generatedCode).replaceAll("  ", "    "));
        }
    }

    /**
     * Methods the same as the {@link Reflector#printStructure(Class)}, but
     * @return generated code as a String instead of printing it into file
     * */
    public static String getGeneratedCode(@NotNull Class<?> someClass) {
        Set<String> packages = new TreeSet<>();
        String generatedCode = generateCode(someClass, packages);
        StringBuilder importPackagesCode = new StringBuilder();
        for (var necessaryPackage : packages) {
            importPackagesCode
                .append("import ")
                .append(necessaryPackage)
                .append(";\n");
        }
        return importPackagesCode + generatedCode;
    }

    /*
    * Method generates code of someClass, stores it in the String and returns generated String
    * @param packages needed for storing necessary packages for correct compilation
    * */
    @NotNull private static String generateCode(@NotNull Class<?> someClass, @NotNull Set<String> packages) {
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
            getAllSubClasses(someClass, packages)
        );

        // ending parenthesis
        someClassCode.append("}\n");

        return someClassCode.toString();
    }

    @NotNull private static String getClassDeclaration(@NotNull Class<?> someClass, @NotNull final Set<String> packages) {
        return getDeclarationModifiers(someClass.getModifiers()) +
                (someClass.isInterface() ? "" : "class ") +
                someClass.getSimpleName() + " " +
                getFullGenericArguments(someClass.getTypeParameters()) +
                getSuperClassInformation(someClass, packages) +
                getInterfacesForImplementing(someClass, packages) +
                "{\n";
    }

    @NotNull private static String getAllSubClasses(@NotNull Class<?> someClass, @NotNull Set<String> packages) {
        StringBuilder classes = new StringBuilder();
        for (var clazz : someClass.getDeclaredClasses()) {
            classes.append(Reflector.generateCode(clazz, packages)).append("\n");
        }
        return classes.toString();
    }

    @NotNull private static String getParametersDescribing(@NotNull Type[] parameters, boolean isNestedClass) {
        var stream = isNestedClass ? Arrays.stream(parameters).skip(1) : Arrays.stream(parameters);
        final Counter counter = new Counter();
        return stream
                .map(p -> {
                    counter.increment();
                    return p.getTypeName() + " a" + counter.getCounter();
                })
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private static boolean isInner(@NotNull Class<?> clazz) {
        return clazz.getEnclosingClass() != null && !Modifier.isStatic(clazz.getModifiers());
    }

    @NotNull private static String getAllConstructors(@NotNull Class<?> someClass, @NotNull final Set<String> packages) {
        StringBuilder constructors = new StringBuilder();

        for (var constructor : someClass.getDeclaredConstructors()) {
            if (constructor.isSynthetic()) {
                continue;
            }
            constructors
                    .append(Modifier.toString(constructor.getModifiers())).append(" ") // modifiers
                    .append(someClass.getSimpleName()); // name
            constructors
                    .append(getParametersDescribing(constructor.getGenericParameterTypes(), isInner(someClass))) // parameters
                    .append(" ")
                    .append(getExceptionsThrowableFromMethods(constructor.getExceptionTypes(), packages)) // exceptions
                    .append(" {\n")
                    .append("}\n\n");
        }

        return constructors.toString();
    }

    @NotNull private static String getMethodDeclaration(@NotNull Method method, @NotNull final Set<String> packages) {
        StringBuilder methodDeclaration = new StringBuilder();
        methodDeclaration.append(getDeclarationModifiers(method.getModifiers())); // modifiers
        methodDeclaration.append(getFullGenericArguments(method.getTypeParameters())).append(" "); // generic args of returned type
        methodDeclaration.append(method.getGenericReturnType().getTypeName()).append(" "); // return type
        methodDeclaration.append(method.getName()); // method name
        methodDeclaration.append(getParametersDescribing(method.getGenericParameterTypes(), false)).append(" "); // arguments
        methodDeclaration.append(getExceptionsThrowableFromMethods(method.getExceptionTypes(), packages)); // exceptions
        return methodDeclaration.toString();
    }

    @NotNull private static String getAllMethods(@NotNull Class<?> someClass, @NotNull final Set<String> packages) {
        StringBuilder methods = new StringBuilder();
        for (var method : someClass.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            if (someClass.isInterface()) {
                if (method.isDefault()) {
                    methods.append("default ");
                }
            }
            methods.append(getMethodDeclaration(method, packages));
            if (someClass.isInterface() && !method.isDefault()) {
                methods.append(";\n");
            } else {
                methods.append(" {\n");
                if (!void.class.equals(method.getReturnType())) {
                    if (boolean.class.equals(method.getReturnType())) {
                        methods.append("return false;\n");
                    } else if (method.getReturnType().isPrimitive()) {
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

    @NotNull private static String getExceptionsThrowableFromMethods(@NotNull Class<?>[] exceptions, @NotNull final Set<String> packages) {
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

    @NotNull private static String getFieldDeclaration(@NotNull Field field) {
        StringBuilder fieldDeclaration = new StringBuilder();
        fieldDeclaration.append(getDeclarationModifiers(field.getModifiers())); // modifiers
        fieldDeclaration.append(field.getGenericType().getTypeName()).append(" "); // type name
        fieldDeclaration.append(field.getName()); // field name
        return fieldDeclaration.toString();
    }

    @NotNull private static String getAllfields(@NotNull Class<?> someClass, @NotNull final Set<String> packages) {
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

    @NotNull private static String getDeclarationModifiers(int modifiersId) {
        String modifiers = Modifier.toString(modifiersId);
        return modifiers.length() == 0 ? ""  : modifiers + " ";
    }

    /*
     * Method retrieves a super class name (extends ..., if such extension there exists)
     * which are there in class declaration
     * and returns it in java valid format
     * */
    @NotNull private static String getSuperClassInformation(@NotNull Class<?> someClass, @NotNull Set<String> packages) {
        Class<?> superClass = someClass.getSuperclass();
        if (superClass != null && superClass.getSuperclass() != null) {
            packages.add(someClass.getSuperclass().getCanonicalName());
            return "extends " + superClass.getSimpleName() + " ";
        } else {
            return "";
        }
    }

    /*
    * Method retrieves a sequence of interfaces
    * which are there in class declaration
    * and returns it in java valid format
    * */
    @NotNull private static String getInterfacesForImplementing(@NotNull Class<?> someClass, @NotNull final Set<String> packages) {
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

    /*
    * Method gets a type, explores it's bounds
    * and returns full java valid generic type with all dependencies.
    * */
    @NotNull private static String getFullGenericArguments(@NotNull TypeVariable<?>[] typesVariable) {
        if (typesVariable.length == 0) {
            return "";
        }
        StringBuilder arguments = new StringBuilder();
        for (var x : typesVariable) {
            if (arguments.length() > 0) {
                arguments.append(", ");
            }
            arguments.append(x.getTypeName());
            arguments.append(
                Arrays.stream(x.getBounds())
                .map(Type::getTypeName)
                .collect(Collectors.joining(" & ", " extends ", ""))
            );
        }
        return "<" + arguments.toString() + "> ";
    }

    /*
    * Class for helping during creating names for variables,
    * should be use in java streams.
    * */
    private static class Counter {
        private int count;

        public void increment() {
            count++;
        }

        public int getCounter() {
            return count;
        }
    }

    /*
    * Method finds all Strings in leftSet, which does not exist in rightSet
    * and collects all such String in one big string.
    * */
    @NotNull private static String getDifferencesBetwwenHashSets(@NotNull HashSet<String> leftSet, @NotNull HashSet<String> rightSet) {
        StringBuilder log = new StringBuilder();
        for (var x : leftSet) {
            if (!rightSet.contains(x)) {
                log.append("\t").append(x).append("\n");
            }
        }
        return log.toString();
    }

    private static String getDifferenceBetweenClassProperties(Class<?> leftClass, Class<?> rightClass, HashSet<String> leftProperty, HashSet<String> rightProperty) {
        StringBuilder log = new StringBuilder();
        log.append(leftClass.getSimpleName()).append("\n");
        log.append(getDifferencesBetwwenHashSets(leftProperty, rightProperty));
        log.append("\n").append(rightClass.getSimpleName()).append("\n");
        log.append(getDifferencesBetwwenHashSets(rightProperty, leftProperty));
        return log.toString();
    }

    /**
     * {@link Reflector#diffClasses(Class, Class)}
     * @return text of log as a String without printing anything into console
     * */
    @NotNull public static String getDiffernces(@NotNull Class<?> leftClass, @NotNull Class<?> rightClass) {
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

        log.append(
            getDifferenceBetweenClassProperties(leftClass, rightClass, leftFields, rightFields)
        );

        log.append("Methods:\n\n");

        log.append(
            getDifferenceBetweenClassProperties(leftClass, rightClass, leftMethods, rightMethods)
        );

        return log.toString();
    }

    /**
     * Method receives two classes to be compared and prints into standard
     * output stream (console) all the different fields and methods.
     * Format of logging:
     * 1. Fields --
     *      firstly, fields of leftClass, which do not exist in rightClass will be printed
     *      secondly, fields of rightClass, which do not exist in leftClass will be printed
     * 2. Methods -- format the same as the fields
     * */
    public static void diffClasses(@NotNull Class<?> leftClass, @NotNull Class<?> rightClass) {
        System.out.println(getDiffernces(leftClass, rightClass));
    }
}
