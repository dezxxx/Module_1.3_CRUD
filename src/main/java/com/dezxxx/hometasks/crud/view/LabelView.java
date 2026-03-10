package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.util.InputUtil;

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
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("0. Back");

            int choice = InputUtil.readChoice(scanner, "Choose: ");

            try {
                switch (choice) {
                    case 1 -> create();
                    case 2 -> getAll();
                    case 3 -> update();
                    case 4 -> delete();
                    case 0 -> { return; }
                    default -> System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            pause();
        }
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

    private void update() {
        Label label = chooseLabel();

        System.out.print("Enter new label name: ");
        String name = scanner.nextLine();

        Label updated = controller.update(label.getId(), name);
        System.out.println("Updated: " + updated);
    }

    private void delete() {
        Label label = chooseLabel();
        controller.delete(label.getId());
        System.out.println("Deleted (soft)");
    }

    private Label chooseLabel() {
        List<Label> labels = controller.getAll();

        if (labels.isEmpty()) {
            throw new IllegalStateException("No labels found");
        }

        System.out.println("Choose label:");
        for (int i = 0; i < labels.size(); i++) {
            System.out.println((i + 1) + ". " + labels.get(i).getName());
        }

        int choice = InputUtil.readChoice(scanner, "Choose number: ");
        if (choice < 1 || choice > labels.size()) {
            throw new IllegalArgumentException("Invalid label choice");
        }

        return labels.get(choice - 1);
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
