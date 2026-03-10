package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.controller.PostController;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostView {

    private final PostController controller;
    private final LabelController labelController;
    private final Scanner scanner;

    public PostView(PostController controller, LabelController labelController, Scanner scanner) {
        this.controller = controller;
        this.labelController = labelController;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("\n--- POST MENU ---");
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
        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        System.out.print("Enter content: ");
        String content = scanner.nextLine();

        List<Label> labels = chooseLabels();

        Post created = controller.create(title, content, labels);
        System.out.println("Created: " + created);
    }

    private void getAll() {
        List<Post> posts = controller.getAll();

        if (posts.isEmpty()) {
            System.out.println("No posts");
            return;
        }

        posts.forEach(System.out::println);
    }

    private void update() {
        Post post = choosePost();

        System.out.print("Enter new title: ");
        String title = scanner.nextLine();

        System.out.print("Enter new content: ");
        String content = scanner.nextLine();

        List<Label> labels = chooseLabels();

        Post updated = controller.update(post.getId(), title, content, labels);
        System.out.println("Updated: " + updated);
    }

    private void delete() {
        Post post = choosePost();
        controller.delete(post.getId());
        System.out.println("Deleted (soft)");
    }

    private Post choosePost() {
        List<Post> posts = controller.getAll();

        if (posts.isEmpty()) {
            throw new IllegalStateException("No posts found");
        }

        System.out.println("Choose post:");
        for (int i = 0; i < posts.size(); i++) {
            System.out.println((i + 1) + ". " + posts.get(i).getTitle());
        }

        int choice = InputUtil.readChoice(scanner, "Choose number: ");
        if (choice < 1 || choice > posts.size()) {
            throw new IllegalArgumentException("Invalid post choice");
        }

        return posts.get(choice - 1);
    }

    private List<Label> chooseLabels() {
        List<Label> allLabels = labelController.getAll();
        List<Label> selectedLabels = new ArrayList<>();

        if (allLabels.isEmpty()) {
            System.out.println("No labels found.");
            return selectedLabels;
        }

        while (true) {
            System.out.println("\nAvailable labels:");
            for (int i = 0; i < allLabels.size(); i++) {
                System.out.println((i + 1) + ". " + allLabels.get(i).getName());
            }

            System.out.println("0. Finish selecting labels");
            int choice = InputUtil.readChoice(scanner, "Choose label number: ");

            if (choice == 0) {
                break;
            }

            if (choice < 1 || choice > allLabels.size()) {
                System.out.println("Invalid label number.");
                continue;
            }

            Label chosenLabel = allLabels.get(choice - 1);

            if (selectedLabels.stream().noneMatch(l -> l.getId().equals(chosenLabel.getId()))) {
                selectedLabels.add(chosenLabel);
                System.out.println("Added label: " + chosenLabel.getName());
            } else {
                System.out.println("Label already selected.");
            }
        }

        return selectedLabels;
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
