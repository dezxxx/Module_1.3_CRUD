package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.model.Label;

import java.util.List;
import java.util.Scanner;

public class LabelView {
    private final LabelController controller;
    private final Scanner scanner;

    public LabelView(LabelController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("\n--- LABEL MENU ---");
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
        System.out.print("Enter label name: ");
        String name = scanner.nextLine();
        Label created = controller.create(name);
        System.out.println("Created: " + created);
    }

    private void getAll() {
        List<Label> labels = controller.getAll();
        if (labels.isEmpty()) {
            System.out.println("No labels");
            return;
        }
        labels.forEach(System.out::println);
    }

    private void getById() {
        Long id = InputUtil.readLong(scanner, "Enter id: ");
        System.out.println(controller.getById(id));
    }

    private void update() {
        Long id = InputUtil.readLong(scanner, "Enter id: ");
        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.println("Updated: " + controller.update(id, name));
    }

    private void delete() {
        Long id = InputUtil.readLong(scanner, "Enter id: ");
        controller.delete(id);
        System.out.println("Deleted (soft)");
    }

}