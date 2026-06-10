package com.dezxxx.hometasks.crud.util;

import java.util.Scanner;

public final class InputUtil {

    private InputUtil() {}

    public static int readChoice(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public static String readText(Scanner scanner, String prompt, int minLength) {
        while (true) {
            System.out.print(prompt + " (0 to cancel): ");
            String value = scanner.nextLine().trim();
            if (value.equals("0")) throw new UserCancelledException();
            if (value.length() >= minLength) return value;
            System.out.println("Input must be at least " + minLength + " character(s).");
        }
    }

    public static String readUpdatedText(Scanner scanner, String prompt, String currentValue) {
        System.out.print(prompt + " (Enter = keep, 0 = cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) throw new UserCancelledException();
        return input.isEmpty() ? currentValue : input;
    }

    public static boolean readConfirmation(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n, Enter to cancel): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.isEmpty() || input.equals("n")) return false;
            if (input.equals("y")) return true;
            System.out.println("Enter 'y' or 'n'.");
        }
    }

    // Returns Integer.MAX_VALUE for 'n' (next page), Integer.MIN_VALUE for 'p' (prev page)
    public static int readPagedInput(Scanner scanner) {
        while (true) {
            System.out.print("Choose (number / n=next / p=prev): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("n")) return Integer.MAX_VALUE;
            if (input.equals("p")) return Integer.MIN_VALUE;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a number, 'n' for next page, or 'p' for prev page.");
            }
        }
    }
}
