package com.dezxxx.hometasks.crud.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class InputUtil {
    private InputUtil() {}

    public static Long readLong(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = sc.nextLine().trim();
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    // ввод: "1,2,3" -> [1,2,3]
    public static List<Long> readLongList(Scanner sc, String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return List.of();

        String[] parts = s.split(",");
        List<Long> result = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (t.isEmpty()) continue;
            result.add(Long.parseLong(t));
        }
        return result;
    }
}