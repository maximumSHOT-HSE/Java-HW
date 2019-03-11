package ru.hse.surkov;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import ru.hse.test.helperClasses.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ReflectorTest {

    private JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

    private void createFile(Class<?> clazz) {
        assertDoesNotThrow(() -> Reflector.printStructure(clazz));
        assertTrue(Files.exists(Paths.get(clazz.getSimpleName() + ".java")));
    }

    private void deleteFile(Class<?> clazz) {
        assertDoesNotThrow(() -> Files.delete(Paths.get(clazz.getSimpleName() + ".java")));
    }

    private void testCompile(Class<?> clazz) {
        assertEquals(0, javaCompiler.run(null, null, null, new File(clazz.getSimpleName() + ".java").getAbsolutePath()));
    }

    @AfterEach
    void deleteClassFiles() {
        File currentDirectory = new File(".");
        for (File file : Objects.requireNonNull(currentDirectory.listFiles(pathname -> pathname.getName().endsWith(".class")))) {
            try {
                Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
            } catch (IOException ignored) {
                // no processing
            }
        }
    }

    @Test
    void testPairClass() {
        createFile(MyPair.class);
        testCompile(MyPair.class);
        assertDoesNotThrow(() -> deleteFile(MyPair.class));
    }

    @Test
    void testHashTableClass() {
        createFile(HashTable.class);
        testCompile(HashTable.class);
        assertDoesNotThrow(() -> deleteFile(HashTable.class));
    }

    @Test
    void testComplicatedClass() {
        createFile(ComplicatedClass.class);
        testCompile(ComplicatedClass.class);
        assertDoesNotThrow(() -> deleteFile(ComplicatedClass.class));
    }

    @Test
    void testMyBaseClass() {
        createFile(MyBaseClass.class);
        testCompile(MyBaseClass.class);
        assertDoesNotThrow(() -> deleteFile(MyBaseClass.class));
    }

    @Test
    void testMyInterface() {
        createFile(MyInterfaceA.class);
        testCompile(MyInterfaceA.class);
        assertDoesNotThrow(() -> deleteFile(MyInterfaceA.class));
    }

    @Test
    void testMyListClass() {
        createFile(MyList.class);
        testCompile(MyList.class);
        assertDoesNotThrow(() -> deleteFile(MyList.class));
    }
}
