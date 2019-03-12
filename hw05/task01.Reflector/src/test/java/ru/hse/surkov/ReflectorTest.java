package ru.hse.surkov;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.hse.test.helperClasses.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectorTest {

    private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

    @AfterEach
    void deleteClassFiles() {
        // delete all generated *.class files
        File currentDirectory = new File(".");
        for (File file : Objects.requireNonNull(currentDirectory.listFiles(pathname -> pathname.getName().endsWith(".class")))) {
            try {
                Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
            } catch (IOException ignored) {
                // no processing
            }
        }
        // delete all generated *.java files
        for (File file : Objects.requireNonNull(currentDirectory.listFiles(pathname -> pathname.getName().endsWith(".java")))) {
            try {
                Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
            } catch (IOException ignored) {
                // no processing
            }
        }
    }

    private String getNoDiffString(Class<?> leftClass, Class<?> rightClass) {
        StringBuilder log = new StringBuilder();

        log.append("Fields:\n\n");

        log.append(leftClass.getSimpleName()).append("\n");
        log.append("\n").append(rightClass.getSimpleName()).append("\n");

        log.append("Methods:\n\n");

        log.append(leftClass.getSimpleName()).append("\n");
        log.append("\n").append(rightClass.getSimpleName()).append("\n");

        return log.toString();
    }

    void testGenerateCompileLoadDiff(Class<?> clazz) throws MalformedURLException, ClassNotFoundException {
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
}