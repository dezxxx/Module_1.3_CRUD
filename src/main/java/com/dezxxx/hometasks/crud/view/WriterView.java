package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.PostController;
import com.dezxxx.hometasks.crud.controller.WriterController;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WriterView {

    private final WriterController controller;
    private final PostController postController;
    private final Scanner scanner;

    public WriterView(WriterController controller, PostController postController, Scanner scanner) {
        this.controller = controller;
        this.postController = postController;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("\n--- WRITER MENU ---");
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
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();

        List<Post> posts = choosePosts();

        Writer created = controller.create(firstName, lastName, posts);
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

    private void update() {
        Writer writer = chooseWriter();

        System.out.print("Enter new first name: ");
        String firstName = scanner.nextLine();

        System.out.print("Enter new last name: ");
        String lastName = scanner.nextLine();

        List<Post> posts = choosePosts();

        Writer updated = controller.update(writer.getId(), firstName, lastName, posts);
        System.out.println("Updated: " + updated);
    }

    private void delete() {
        Writer writer = chooseWriter();
        controller.delete(writer.getId());
        System.out.println("Deleted (soft)");
    }

    private Writer chooseWriter() {
        List<Writer> writers = controller.getAll();

        if (writers.isEmpty()) {
            throw new IllegalStateException("No writers found");
        }

        System.out.println("Choose writer:");
        for (int i = 0; i < writers.size(); i++) {
            Writer writer = writers.get(i);
            System.out.println((i + 1) + ". " + writer.getFirstName() + " " + writer.getLastName());
        }

        int choice = InputUtil.readChoice(scanner, "Choose number: ");
        if (choice < 1 || choice > writers.size()) {
            throw new IllegalArgumentException("Invalid writer choice");
        }

        return writers.get(choice - 1);
    }

    private List<Post> choosePosts() {
        List<Post> allPosts = postController.getAll();
        List<Post> selectedPosts = new ArrayList<>();

        if (allPosts.isEmpty()) {
            System.out.println("No posts found. Writer will be created without posts.");
            return selectedPosts;
        }

        while (true) {
            System.out.println("\nAvailable posts:");
            for (int i = 0; i < allPosts.size(); i++) {
                Post post = allPosts.get(i);
                System.out.println((i + 1) + ". " + post.getTitle());
            }

            System.out.println("0. Finish");
            int choice = InputUtil.readChoice(scanner, "Choose post number: ");

            if (choice == 0) {
                break;
            }

            if (choice < 1 || choice > allPosts.size()) {
                System.out.println("Invalid post choice");
                continue;
            }

            Post chosenPost = allPosts.get(choice - 1);

            if (selectedPosts.stream().noneMatch(post -> post.getId().equals(chosenPost.getId()))) {
                selectedPosts.add(chosenPost);
                System.out.println("Added: " + chosenPost.getTitle());
            } else {
                System.out.println("This post is already selected");
            }
        }

        return selectedPosts;
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
