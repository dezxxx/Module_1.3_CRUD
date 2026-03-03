package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.WriterController;
import com.dezxxx.hometasks.crud.model.Writer;

import java.util.List;
import java.util.Scanner;

public class WriterView {
    private final WriterController controller;
    private final Scanner scanner;

    public WriterView(WriterController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("\n--- WRITER MENU ---");
            System.out.println("1. Create");
            System.out.println("2. Get all");
            System.out.println("3. Get by id");
            System.out.println("4. Update");
            System.out.println("5. Delete");
            System.out.println("0. Back");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> create();
                    case "2" -> getAll();
                    case "3" -> getById();
                    case "4" -> update();
                    case "5" -> delete();
                    case "0" -> { return; }
                    default -> System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            pause();  // ← ВАЖНО: всегда после выполнения
        }
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void create() {
        System.out.print("Enter firstName: ");
        String fn = scanner.nextLine();
        System.out.print("Enter lastName: ");
        String ln = scanner.nextLine();
        Writer created = controller.create(fn, ln);
        System.out.println("Created: " + created);
    }

    private void getAll() {
        List<Writer> writers = controller.getAll();
        if (writers.isEmpty()) {
            System.out.println("No writers");
            return;
        }
        writers.forEach(System.out::println);
    }

    private void getById() {
        Long id = InputUtil.readLong(scanner, "Enter id: ");
        System.out.println(controller.getById(id));
    }

    private void update() {
        Long id = InputUtil.readLong(scanner, "Enter id: ");
        System.out.print("Enter new firstName: ");
        String fn = scanner.nextLine();
        System.out.print("Enter new lastName: ");
        String ln = scanner.nextLine();
        System.out.println("Updated: " + controller.update(id, fn, ln));
    }

    private void delete() {
        Long id = InputUtil.readLong(scanner, "Enter id: ");
        controller.delete(id);
        System.out.println("Deleted (soft)");
    }
}