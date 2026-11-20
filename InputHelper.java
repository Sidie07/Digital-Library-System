package ui;

import java.util.Scanner;

public class InputHelper {
    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input — please enter a whole number.");
            }
        }
    }

    public static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) return value;
            System.out.printf("  Please enter a number between %d and %d.%n", min, max);
        }
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("  This field cannot be empty.");
        }
    }

    public static String readDate(String prompt) {
        while (true) {
            String value = readLine(prompt + " (YYYY-MM-DD): ");
            if (value.matches("\\d{4}-\\d{2}-\\d{2}")) return value;
            System.out.println("  Invalid format. Use YYYY-MM-DD (e.g. 2025-06-15).");
        }
    }

    public static boolean confirm(String prompt) {
        while (true) {
            String input = readLine(prompt + " (yes/no): ").toLowerCase();
            if (input.equals("yes") || input.equals("y")) return true;
            if (input.equals("no")  || input.equals("n")) return false;
            System.out.println("  Please type yes or no.");
        }
    }

    public static String chooseFromOptions(String prompt, String[] options) {
        System.out.println(prompt);
        for (int i = 0; i < options.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, options[i]);
        }
        int choice = readIntInRange("  Enter choice: ", 1, options.length);
        return options[choice - 1];
    }

    public static void pause() {
        readLine("\nPress ENTER to continue...");
    }
}
