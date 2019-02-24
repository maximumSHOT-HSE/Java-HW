package ru.hse.surkov.hw04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {

    private PhoneBook phoneBook;

    @BeforeEach
    void setUp() {
        phoneBook = new PhoneBook("TEST", true);
    }

    @Test
    void testContainsEmptyPhoneBook() {
        assertFalse(phoneBook.contains("A", "1"));
        assertFalse(phoneBook.contains("1", "A"));
        assertFalse(phoneBook.contains("Aerkjnkarjr", "12395709483"));
    }

    @Test
    void testAddRecordPhoneBook() {
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 50;phoneNumber < 100; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                    iter == 0,
                        phoneBook.addRecord(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
    }

    @Test
    void testContainsFilledPhoneBook() {
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            iter == 0,
                            phoneBook.addRecord(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
        for (char name = 'a'; name <= 'z'; name++) {
            for (int phoneNumber = 0;phoneNumber < 20; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            name <= 'h' && 5 <= phoneNumber && phoneNumber < 10,
                            phoneBook.contains(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
    }

    @Test
    void testDeleteRecord() {
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            iter == 0,
                            phoneBook.addRecord(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
        for (char name = 'a'; name <= 'h'; name += 2) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber += 2) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            iter == 0,
                            phoneBook.deleteRecord(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
        for (char name = 'a'; name <= 'z'; name++) {
            for (int phoneNumber = 0;phoneNumber < 20; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            name <= 'h' && 5 <= phoneNumber &&
                                    phoneNumber < 10 &&
                                    (name % 2 != 'a' % 2 || phoneNumber % 2 != 5 % 2),
                            phoneBook.contains(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
    }

    @Test
    void testChangeName() {
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            iter == 0,
                            phoneBook.addRecord(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    for (char newName = 'a'; newName <= 'h'; newName++) {
                        assertFalse(phoneBook.changeName(
                                Character.toString(name),
                                Integer.toString(phoneNumber),
                                Character.toString(newName)
                        ));
                    }
                }
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            iter == 0,
                            phoneBook.changeName(
                                Character.toString(name),
                                Integer.toString(phoneNumber),
                                name + "!"
                            )
                    );
                }
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertFalse(
                        phoneBook.contains(
                            Character.toString(name),
                            Integer.toString(phoneNumber)
                        )
                    );
                    assertTrue(
                        phoneBook.contains(
                            name + "!",
                            Integer.toString(phoneNumber)
                        )
                    );
                }
            }
        }
    }

    @Test
    void testChangePhoneNumber() {
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                            iter == 0,
                            phoneBook.addRecord(Character.toString(name), Integer.toString(phoneNumber))
                    );
                }
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    for (int newPhneNumber = 5; newPhneNumber < 10; newPhneNumber++) {
                        assertFalse(phoneBook.changePhoneNumber(
                                Character.toString(name),
                                Integer.toString(phoneNumber),
                                Integer.toString(newPhneNumber)
                        ));
                    }
                }
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertEquals(
                        iter == 0,
                        phoneBook.changePhoneNumber(
                            Character.toString(name),
                            Integer.toString(phoneNumber),
                            phoneNumber + "!"
                        )
                    );
                }
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 5;phoneNumber < 10; phoneNumber++) {
                for (int iter = 0;iter < 3;iter++) {
                    assertFalse(
                            phoneBook.contains(
                                    Character.toString(name),
                                    Integer.toString(phoneNumber)
                            )
                    );
                    assertTrue(
                            phoneBook.contains(
                                    Character.toString(name),
                                    phoneNumber + "!"
                            )
                    );
                }
            }
        }
    }
}
