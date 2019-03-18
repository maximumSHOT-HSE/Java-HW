package ru.hse.surkov;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.hse.surkov.helperClasses.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectorTest {

    @AfterEach
    void deleteClassFiles() {
        // delete all generated *.class files
        File currentDirectory = new File(".");
        for (File file : Objects.requireNonNull(
                currentDirectory.listFiles(
                        pathname -> pathname.getName().endsWith(".class")))) {
            try {
                Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
            } catch (IOException ignored) {
                // no processing
            }
        }
        // delete all generated *.java files
        for (File file : Objects.requireNonNull(
                currentDirectory.listFiles(
                        pathname -> pathname.getName().endsWith(".java")))) {
            try {
                Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
            } catch (IOException ignored) {
                // no processing
            }
        }
    }

    private String getNoDiffString(Class<?> leftClass, Class<?> rightClass) {
        StringBuilder log = new StringBuilder();

        log
        .append("Fields:")
        .append(System.lineSeparator())
        .append(System.lineSeparator());

        log
        .append(leftClass.getSimpleName())
        .append(System.lineSeparator());

        log.
        append(System.lineSeparator())
        .append(rightClass.getSimpleName())
        .append(System.lineSeparator());

        log
        .append("Methods:")
        .append(System.lineSeparator())
        .append(System.lineSeparator());

        log
        .append(leftClass.getSimpleName())
        .append(System.lineSeparator());

        log
        .append(System.lineSeparator())
        .append(rightClass.getSimpleName())
        .append(System.lineSeparator());

        return log.toString();
    }

    void testGenerateCompileLoadDiff(Class<?> clazz) throws
            MalformedURLException, ClassNotFoundException {
        assertDoesNotThrow(() -> Reflector.printStructure(clazz));
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        javaCompiler.run(null, null, null, clazz.getSimpleName() + ".java");

        URLClassLoader urlClassLoader = URLClassLoader.newInstance(new URL[] {
            new File(".").toURI().toURL()
        });

        Class<?> generatedClazz = Class.forName(clazz.getSimpleName(), true, urlClassLoader);

        assertEquals(getNoDiffString(clazz, clazz), Reflector.getDiffernces(clazz, generatedClazz));
        assertEquals(getNoDiffString(clazz, clazz), Reflector.getDiffernces(generatedClazz, clazz));
    }

    @Test
    void testPairClass() {
        assertDoesNotThrow(() -> testGenerateCompileLoadDiff(MyPair.class));
    }

    @Test
    void testHashTable() {
        assertDoesNotThrow(() -> testGenerateCompileLoadDiff(HashTable.class));
    }

    @Test
    void testMyList() {
        assertDoesNotThrow(() -> testGenerateCompileLoadDiff(MyList.class));
    }

    @Test
    void testComplicatedClass() {
        assertDoesNotThrow(() -> testGenerateCompileLoadDiff(ComplicatedClass.class));
    }

    @Test
    void testMyBaseClass() {
        assertDoesNotThrow(() -> testGenerateCompileLoadDiff(MyBaseClass.class));
    }

    @Test
    void tesyMyInterface() {
        assertDoesNotThrow(() -> testGenerateCompileLoadDiff(MyInterfaceA.class));
    }

    private static String deleteSeparators(String string) {
        return
            string
            .chars()
            .filter(c ->
                    !Character.isSpaceChar(c) &&
                    (char) c != '\t' &&
                    !Character.toString(c).equals(System.lineSeparator()))
            .mapToObj(Character::toString)
            .collect(Collectors.joining());
    }

    void testDifference(Class<?> leftClass, Class<?> rightClass, String fileWithDifferences) throws FileNotFoundException {
        String generatedDifference = Reflector.getDiffernces(leftClass, rightClass);
        generatedDifference = deleteSeparators(generatedDifference);
        try (Scanner scanner = new Scanner(new FileInputStream(fileWithDifferences))) {
            StringBuilder helper = new StringBuilder();
            while (scanner.hasNextLine()) {
                helper.append(scanner.nextLine()).append(System.lineSeparator());
            }
            String expectedDifferences = deleteSeparators(helper.toString());
            assertEquals(expectedDifferences, generatedDifference);
        }
    }

    @Test
    void testDifferenceBetweenEmptyInterfaces() {
        assertDoesNotThrow(() -> testDifference(
            MyInterfaceA.class, MyInterfaceB.class,
            "./src/test/resources/testSimpleDifference"
        ));
    }

    @Test
    void testDiffGenericArguments() {
        assertDoesNotThrow(() -> testDifference(
            A.class, B.class,
            "./src/test/resources/testDiffGenericArguments"
        ));
    }

    @Test
    void testDifferencesFields() {
        assertDoesNotThrow(() -> testDifference(
            FieldA.class, FieldB.class,
            "./src/test/resources/testDifferencesFields"
        ));
    }

    @Test
    void testDifferencesMethods() {
        assertDoesNotThrow(() -> testDifference(
            MethodsA.class, MethodsB.class,
            "./src/test/resources/testDifferencesMethods"
        ));
    }
}
