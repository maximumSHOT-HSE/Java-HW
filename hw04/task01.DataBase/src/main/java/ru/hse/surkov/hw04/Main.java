package ru.hse.surkov.hw04;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLOutput;
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
            "7 - print all records\n";

    private enum CommandInterfaceStatus {FINISHED, IN_PROCESS, READY_TO_WORK};
    @NotNull private static CommandInterfaceStatus commandInterfaceStatus = CommandInterfaceStatus.READY_TO_WORK;
    @NotNull private static Scanner inputScanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("|--------Console phone book--------|\n" + COMMAND_INTERFACE);
        commandInterfaceStatus = CommandInterfaceStatus.IN_PROCESS;
        while (commandInterfaceStatus != CommandInterfaceStatus.FINISHED) {
            System.out.print(">");
            int queryType = -1;
            try {
                queryType = inputScanner.nextInt();
            } catch (Exception ignored) {
                // Homework statement implies it can not happen
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
                System.out.println("Unsupported command. Please, try again!");
            }
        }
    }

    private static void exit() {

    }

    private static void addRecord() {

    }

    private static void findPhoneNumbersByName() {

    }

    private static void findNamesByPhoneNumber() {

    }

    private static void deleteRecord() {

    }

    private static void changeFieldNameOfRecord() {

    }

    private static void changeFieldPhoneNumberOfRecord() {

    }

    private static void printAllRecords() {

    }
}
