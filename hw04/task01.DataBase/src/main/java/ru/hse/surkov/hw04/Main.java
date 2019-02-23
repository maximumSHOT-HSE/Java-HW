package ru.hse.surkov.hw04;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public final class Main {

    private Main() {

    }

    @NotNull private static final String COMMAND_INTERFACE =
            "0 - exit\n" +
            "1 - add record (name and phone number)\n" +
            "2 - find phone numbers by name\n" +
            "3 - find names by phone number\n" +
            "4 - delete record (name and phone number)\n" +
            "5 - change field 'name' of record\n" +
            "6 - change field 'phone number' of record\n" +
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
    @NotNull private static String UNSUPPOERTED_COMMAND = "Unsupported command. Please, try again!";
    @NotNull private static String NUMBER_OF_RECORDS = "number of records";

    private enum CommandInterfaceStatus {FINISHED, IN_PROCESS, READY_TO_WORK};
    @NotNull private static CommandInterfaceStatus commandInterfaceStatus = CommandInterfaceStatus.READY_TO_WORK;
    @NotNull private static Scanner inputScanner = new Scanner(System.in);
    @NotNull private static PhoneBook phoneBook = new PhoneBook();

    public static void main(String[] args) {
        System.out.println(APPLICATION_NAME + "\n" + DESCIPTION + "\n" + COMMAND_INTERFACE + "\n");
        commandInterfaceStatus = CommandInterfaceStatus.IN_PROCESS;
        setPhoneBookName();
        while (commandInterfaceStatus != CommandInterfaceStatus.FINISHED) {
            int queryType;
            String nextQuery;
            try {
                nextQuery = inputScanner.next();
                queryType = Integer.parseInt(nextQuery);
            } catch (NumberFormatException ignored) {
                System.out.println(UNSUPPOERTED_COMMAND);
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
                System.out.println(UNSUPPOERTED_COMMAND);
            }
        }
    }

    public static void setPhoneBookName() {
        System.out.print("Enter phone book global name: ");
        String phoneBookName = inputScanner.next();
        phoneBook.setPhoneBookName(phoneBookName);
        System.out.println(DONE);
    }

    private static void exit() {
        commandInterfaceStatus = CommandInterfaceStatus.FINISHED;
    }

    private static void addRecord() {
        System.out.println(ENTER_NAME_AND_PHONE_NUMBER);
        String name = inputScanner.next();
        String phoneNumber = inputScanner.next();
        phoneBook.addRecord(name, phoneNumber);
        System.out.println(DONE);
    }

    private static void findPhoneNumbersByName() {
        System.out.print(ENTER_NAME);
        String name = inputScanner.next();
        List<String> phoneNumbers = phoneBook.getPhoneNumbersByName(name);
        for (int i = 0; i < phoneNumbers.size(); i++) {
            System.out.println((i + 1) + ". " + phoneNumbers.get(i));
        }
        System.out.println(DONE);
    }

    private static void findNamesByPhoneNumber() {
        System.out.print(ENTER_PHONE_NUMBER);
        String phoneNumber = inputScanner.next();
        List<String> names = phoneBook.getNamesByPhoneNumber(phoneNumber);
        for (int i = 0; i < names.size(); i++) {
            System.out.println((i + 1) + ". " + names.get(i));
        }
        System.out.println(DONE);
    }

    private static void deleteRecord() {
        System.out.println(ENTER_NAME);
        String name = inputScanner.next();
        System.out.println(ENTER_PHONE_NUMBER);
        String phoneNumber = inputScanner.next();
        System.out.println(DELETING_RECORD);
        phoneBook.deleteRecord(name, phoneNumber);
        System.out.println(DONE);
    }

    private static void changeFieldNameOfRecord() {
        System.out.println(ENTER_NAME_AND_PHONE_NUMBER);
        String name = inputScanner.next();
        String phoneNumber = inputScanner.next();
        System.out.println(ENTER + " new " + FIELD_NAME + ": ");
        String newName = inputScanner.next();

        phoneBook.changeName(name, phoneNumber, newName);
        System.out.println(FIELD_NAME + " " + HAS_BEEN_CHANGED);
    }

    private static void changeFieldPhoneNumberOfRecord() {
        System.out.println(ENTER_NAME_AND_PHONE_NUMBER);
        String name = inputScanner.next();
        String phoneNumber = inputScanner.next();
        System.out.println(ENTER + " new " + FIELD_PHONE_NUMBER + ": ");
        String newPhoneNumber = inputScanner.next();

        phoneBook.changePhoneNumber(name, phoneNumber, newPhoneNumber);
        System.out.println(FIELD_PHONE_NUMBER + " " + HAS_BEEN_CHANGED);
    }

    private static void printAllRecords() {
        System.out.println(PRINTING);
        List<Record> allRecords = phoneBook.getAllRecords();
        System.out.println(NUMBER_OF_RECORDS + ": " + allRecords.size());
        for (int i = 0; i < allRecords.size(); i++) {
            System.out.println((i + 1) + ". " + allRecords.get(i));
        }
    }
}
