package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.util.DatabaseType;
import com.dezxxx.hometasks.crud.util.InputUtil;

import java.util.Scanner;

public class DbSelectionView {

    private final Scanner scanner;

    public DbSelectionView(Scanner scanner) {
        this.scanner = scanner;
    }

    public DatabaseType select() {
        while (true) {
            System.out.println("\n=== Select Database ===");
            System.out.println("1. MySQL");
            System.out.println("2. PostgreSQL");

            int choice = InputUtil.readChoice(scanner, "Choose: ");

            switch (choice) {
                case 1 -> { return DatabaseType.MYSQL; }
                case 2 -> { return DatabaseType.POSTGRES; }
                default -> System.out.println("Enter 1 or 2.");
            }
        }
    }
}
