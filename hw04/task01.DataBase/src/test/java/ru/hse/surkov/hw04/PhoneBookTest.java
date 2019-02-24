package ru.hse.surkov.hw04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.LogManager;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PhoneBookTest {

    static {
        LogManager.getLogManager().reset(); // disable all loggers (for testing UI)
    }

    private PhoneBook phoneBook;

    @BeforeEach
    void setUp() {
        phoneBook = new PhoneBook("TEST");
    }

    @Test
    void testContainsNulls() {
        assertThrows(IllegalArgumentException.class, () -> phoneBook.contains(null, "?"));
        assertThrows(IllegalArgumentException.class, () -> phoneBook.contains("?", null));
        assertThrows(IllegalArgumentException.class, () -> phoneBook.contains(null, null));
    }

    @Test
    void testAddRecordNulls() {
        assertThrows(IllegalArgumentException.class, () -> phoneBook.addRecord(null, "?"));
        assertThrows(IllegalArgumentException.class, () -> phoneBook.addRecord("?", null));
        assertThrows(IllegalArgumentException.class, () -> phoneBook.addRecord(null, null));
    }
}
