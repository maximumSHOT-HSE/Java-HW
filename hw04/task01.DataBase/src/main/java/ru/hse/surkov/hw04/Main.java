package ru.hse.surkov.hw04;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class Main {

    private Main() {

    }

    @NotNull private static final String COMMAND_INTERFACE =
            "0 - exit" + System.lineSeparator() +
            "1 - add record (name and phone number)" + System.lineSeparator() +
            "2 - find phone numbers by name" + System.lineSeparator() +
            "3 - find names by phone number" + System.lineSeparator() +
            "4 - delete record (name and phone number)" + System.lineSeparator() +
            "5 - change field 'name' of record" + System.lineSeparator() +
            "6 - change field 'phone number' of record" + System.lineSeparator() +
            "7 - print all records";

    @NotNull private static String APPLICATION_NAME = "|--------Console phone book--------|";
    @NotNull private static String DESCIPTION = "Software for storing records consisting of name and phone number";
    @NotNull private static String ENTER = "enter";
    @NotNull private static String FIELD_NAME = "name";
    @NotNull private static String FIELD_PHONE_NUMBER = "phone number";
    @NotNull private static String ENTER_NAME = ENTER + " " + FIELD_NAME + ": ";
    @NotNull private static String ENTER_PHONE_NUMBER = ENTER + " " + FIELD_PHONE_NUMBER + ": ";
    @NotNull private static String ENTER_NAME_AND_PHONE_NUMBER = ENTER + " " + FIELD_NAME + " and " + FIELD_PHONE_NUMBER + " (each field in separate line)";
    @NotNull private static String DELETING_RECORD = "Deleting record...";
    @NotNull private static String HAS_BEEN_CHANGED = "has been changed";
    @NotNull private static String PRINTING = "Printing...";
    @NotNull private static String DONE = "done";
    @NotNull private static String UNSUPPORTED_COMMAND = "Unsupported command. Please, try again!";
    @NotNull private static String NUMBER_OF_RECORDS = "number of records";
    @NotNull private static String RECORD = "record";
    @NotNull private static String RECORD_ALREADY_EXISTS = RECORD + " already exists";
    @NotNull private static String AMOUNT = "amount";
    @NotNull private static String DOES_NOT_EXISTS = RECORD + " does not exists";
    @NotNull private static String FINISHING = "Finishing...";

    private enum CommandInterfaceStatus {FINISHED, IN_PROCESS, READY_TO_WORK};
    @NotNull private static CommandInterfaceStatus commandInterfaceStatus = CommandInterfaceStatus.READY_TO_WORK;
    @NotNull private static Scanner inputScanner = new Scanner(System.in);
    @NotNull private static PhoneBook phoneBook;

    public static void main(String[] args) {
        System.out.println(APPLICATION_NAME + System.lineSeparator() + DESCIPTION + System.lineSeparator() + COMMAND_INTERFACE + System.lineSeparator());
        commandInterfaceStatus = CommandInterfaceStatus.IN_PROCESS;
        createPhoneBook();
        while (commandInterfaceStatus != CommandInterfaceStatus.FINISHED) {
            int queryType;
            String nextQuery;
            try {
                nextQuery = inputScanner.next();
                queryType = Integer.parseInt(nextQuery);
            } catch (NumberFormatException ignored) {
                System.out.println(UNSUPPORTED_COMMAND);
                continue;
            }
            if (queryType == 0) { // exit
                Main.exit();
            } else if (queryType == 1) { // add record
                addRecord();
            } else if (queryType == 2) { // find phone numbers by name
                findPhoneNumbersByName();
            } else if (queryType == 3) { // find names by phone number
                findNamesByPhoneNumber();
            } else if (queryType == 4) { // delete record
                deleteRecord();
            } else if (queryType == 5) { // change field 'name' of record
                changeFieldNameOfRecord();
            } else if (queryType == 6) { // change field 'phone number' of record
                changeFieldPhoneNumberOfRecord();
            } else if (queryType == 7) { // print all records
                printAllRecords();
            } else { // unsupported
                System.out.println(UNSUPPORTED_COMMAND);
            }
        }
    }

    public static void createPhoneBook() {
        System.out.print("Enter phone book global name: ");
        String phoneBookName = inputScanner.next();
        phoneBook = new PhoneBook(phoneBookName, false);
        System.out.println(DONE);
    }

    private static void exit() {
        System.out.println(FINISHING);
        commandInterfaceStatus = CommandInterfaceStatus.FINISHED;
    }

    private static void addRecord() {
        System.out.println(ENTER_NAME_AND_PHONE_NUMBER);
        String name = inputScanner.next();
        String phoneNumber = inputScanner.next();
        if (phoneBook.addRecord(name, phoneNumber)) {
            System.out.println(DONE);
        } else {
            System.out.println(RECORD_ALREADY_EXISTS);
        }
    }

    private static void findPhoneNumbersByName() {
        System.out.print(ENTER_NAME);
        String name = inputScanner.next();
        List<Record> phoneNumbers = phoneBook.getPhoneNumbersByName(name);
        System.out.println(AMOUNT + " of " + FIELD_PHONE_NUMBER + "s: " + phoneNumbers.size());
        for (int i = 0; i < phoneNumbers.size(); i++) {
            System.out.println((i + 1) + ". " + phoneNumbers.get(i).getPhoneNumber());
        }
        System.out.println(DONE);
    }

    private static void findNamesByPhoneNumber() {
        System.out.print(ENTER_PHONE_NUMBER);
        String phoneNumber = inputScanner.next();
        List<Record> names = phoneBook.getNamesByPhoneNumber(phoneNumber);
        System.out.println(AMOUNT + " of " + FIELD_NAME + "s: " + names.size());
        for (int i = 0; i < names.size(); i++) {
            System.out.println((i + 1) + ". " + names.get(i).getName());
        }
        System.out.println(DONE);
    }

    private static void deleteRecord() {
        System.out.println(ENTER_NAME_AND_PHONE_NUMBER);
        String name = inputScanner.next();
        String phoneNumber = inputScanner.next();
        if (!phoneBook.contains(name, phoneNumber)) {
            System.out.println(DOES_NOT_EXISTS);
            return;
        } else {
            System.out.println(DELETING_RECORD);
            phoneBook.deleteRecord(name, phoneNumber);
            System.out.println(DONE);
        }
    }

    private static void changeField(String fieldName, TriFunction<String, String, String, Boolean> changeFieldMethod) {
        System.out.println(ENTER_NAME_AND_PHONE_NUMBER);
        String name = inputScanner.next();
        String phoneNumber = inputScanner.next();
        if (!phoneBook.contains(name, phoneNumber)) {
            System.out.println(DOES_NOT_EXISTS);
            return;
        }
        System.out.println(ENTER + " new " + fieldName + ": ");
        String newFieldValue = inputScanner.next();
        if (changeFieldMethod.apply(name, phoneNumber, newFieldValue)) {
            System.out.println(fieldName + " " + HAS_BEEN_CHANGED);
        } else {
            System.out.println(RECORD_ALREADY_EXISTS);
        }
    }

    private static void changeFieldNameOfRecord() {
        changeField(FIELD_NAME, phoneBook::changeName);
    }

    private static void changeFieldPhoneNumberOfRecord() {
        changeField(FIELD_PHONE_NUMBER, phoneBook::changePhoneNumber);
    }

    private static void printAllRecords() {
        System.out.println(PRINTING);
        List<Record> allRecords = phoneBook.getAllRecords();
        System.out.println(NUMBER_OF_RECORDS + ": " + allRecords.size());
        for (int i = 0; i < allRecords.size(); i++) {
            System.out.println((i + 1) + ". " + allRecords.get(i));
        }
        System.out.println(DONE);
    }
}
