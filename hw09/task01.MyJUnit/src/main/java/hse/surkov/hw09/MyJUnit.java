package hse.surkov.hw09;

import java.util.Scanner;

public class MyJUnit {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String className = scanner.nextLine();
        try {
            Class<?> loadedClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Sorry, but class not found.");
            System.exit(1);
        }

    }
}
