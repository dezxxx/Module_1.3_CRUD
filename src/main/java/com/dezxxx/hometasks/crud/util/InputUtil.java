package com.dezxxx.hometasks.crud.util;

import java.util.Scanner;

public final class InputUtil {

    private InputUtil() {
    }

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
}
