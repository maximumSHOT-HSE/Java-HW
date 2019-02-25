package ru.hse.surkov.hw04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {

    private PhoneBook phoneBook;

    @BeforeEach
    void setUp() {
        phoneBook = new PhoneBook("TestPhoneBook", true);
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
                    if (iter == 0) {
                        assertTrue(
                            phoneBook.changeName(
                                    Character.toString(name),
                                    Integer.toString(phoneNumber),
                                    name + "!"
                            )
                        );
                    } else {
                        char finalName = name;
                        int finalPhoneNumber = phoneNumber;
                        assertThrows(
                            NoSuchElementException.class,
                            () -> phoneBook.changeName(
                                    Character.toString(finalName),
                                    Integer.toString(finalPhoneNumber),
                                    finalName + "!"
                            )
                        );
                    }
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
    void testChangeNameException() {
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
        for (char name = 'i'; name <= 'z'; name++) {
            char finalName = name;
            assertThrows(NoSuchElementException.class, () -> phoneBook.changeName(Character.toString(finalName), "1", "aaa"));
        }
        for (int phoneNumber = 10; phoneNumber < 20; phoneNumber++) {
            int finalPhoneNumber = phoneNumber;
            assertThrows(NoSuchElementException.class, () -> phoneBook.changeName("a", Integer.toString(finalPhoneNumber), "aaa"));
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
                    if (iter == 0) {
                        assertTrue(
                            phoneBook.changePhoneNumber(
                                    Character.toString(name),
                                    Integer.toString(phoneNumber),
                                    phoneNumber + "!"
                            )
                        );
                    } else {
                        char finalName = name;
                        int finalPhoneNumber = phoneNumber;
                        assertThrows(
                            NoSuchElementException.class,
                            () -> phoneBook.changePhoneNumber(
                                    Character.toString(finalName),
                                    Integer.toString(finalPhoneNumber),
                                    finalPhoneNumber + "!"
                            )
                        );
                    }
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

    @Test
    void testChangePhoneNumberException() {
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
        for (char name = 'i'; name <= 'z'; name++) {
            char finalName = name;
            assertThrows(NoSuchElementException.class, () -> phoneBook.changePhoneNumber(Character.toString(finalName), "1", "111"));
        }
        for (int phoneNumber = 10; phoneNumber < 20; phoneNumber++) {
            int finalPhoneNumber = phoneNumber;
            assertThrows(NoSuchElementException.class, () -> phoneBook.changePhoneNumber("a", Integer.toString(finalPhoneNumber), "111"));
        }
    }

    @Test
    void testGetAllRecordsSimple() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
                expectedList.add(new Record(sname, sphoneNumber));
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getAllRecords().toArray());
    }

    @Test
    void testGetAllRecordsWithDeleteOperations() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
                expectedList.add(new Record(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    continue;
                }
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                expectedList.remove(new Record(sname, sphoneNumber));
                assertTrue(phoneBook.deleteRecord(sname, sphoneNumber));
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getAllRecords().toArray());
    }

    @Test
    void testGetAllRecordsWithChangeOperations() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
                expectedList.add(new Record(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    continue;
                }
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                int position = (name - 'a') * 5 + phoneNumber;
                expectedList.get(position).setName(sname + "!" + sname);
                expectedList.get(position).setPhoneNumber(sphoneNumber + "?" + sphoneNumber);
                assertTrue(phoneBook.changeName(sname, sphoneNumber, sname + "!" + sname));
                assertTrue(phoneBook.changePhoneNumber(sname + "!" + sname, sphoneNumber, sphoneNumber + "?" + sphoneNumber));
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getAllRecords().toArray());
    }

    @Test
    void testGetPhoneNumbersByNameSimple() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
                if (sname.equals("a")) {
                    expectedList.add(new Record(sname, sphoneNumber));
                }
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getPhoneNumbersByName("a").toArray());
    }

    @Test
    void testGetPhoneNumbersByNameWithDeleteOperations() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    continue;
                }
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.deleteRecord(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    String sname = Character.toString(name);
                    String sphoneNumber = Integer.toString(phoneNumber);
                    if (sname.equals("g")) {
                        expectedList.add(new Record(sname, sphoneNumber));
                    }
                }
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getPhoneNumbersByName("g").toArray());
    }

    @Test
    void testGetPhoneNumbersByNameWithChangeOperations() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    continue;
                }
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.changeName(sname, sphoneNumber, sname + "!" + sname));
                assertTrue(phoneBook.changePhoneNumber(sname + "!" + sname, sphoneNumber, sphoneNumber + "?" + sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                if (!(name % 2 == 0 || phoneNumber % 2 == 1)) {
                    sname = sname + "!" + sname;
                    sphoneNumber = sphoneNumber + "?" + sphoneNumber;
                }
                if (sname.equals("g!g")) {
                    expectedList.add(new Record(sname, sphoneNumber));
                }
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getPhoneNumbersByName("g!g").toArray());
    }

    @Test
    void testGetNamesByPhoneNumberSimple() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
                if (sphoneNumber.equals("3")) {
                    expectedList.add(new Record(sname, sphoneNumber));
                }
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getNamesByPhoneNumber("3").toArray());
    }

    @Test
    void testGetNamesByPhoneNumberWithDeleteOperations() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    continue;
                }
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.deleteRecord(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    String sname = Character.toString(name);
                    String sphoneNumber = Integer.toString(phoneNumber);
                    if (sphoneNumber.equals("2")) {
                        expectedList.add(new Record(sname, sphoneNumber));
                    }
                }
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getNamesByPhoneNumber("2").toArray());
    }

    @Test
    void testGetNamesByPhoneNumberWithChangeOperations() {
        List<Record> expectedList = new ArrayList<>();
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.addRecord(sname, sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                if (name % 2 == 0 || phoneNumber % 2 == 1) {
                    continue;
                }
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                assertTrue(phoneBook.changeName(sname, sphoneNumber, sname + "!" + sname));
                assertTrue(phoneBook.changePhoneNumber(sname + "!" + sname, sphoneNumber, sphoneNumber + "?" + sphoneNumber));
            }
        }
        for (char name = 'a'; name <= 'h'; name++) {
            for (int phoneNumber = 0; phoneNumber < 5; phoneNumber++) {
                String sname = Character.toString(name);
                String sphoneNumber = Integer.toString(phoneNumber);
                if (!(name % 2 == 0 || phoneNumber % 2 == 1)) {
                    sname = sname + "!" + sname;
                    sphoneNumber = sphoneNumber + "?" + sphoneNumber;
                }
                if (sphoneNumber.equals("2?2")) {
                    expectedList.add(new Record(sname, sphoneNumber));
                }
            }
        }
        assertArrayEquals(expectedList.toArray(), phoneBook.getNamesByPhoneNumber("2?2").toArray());
    }
}
