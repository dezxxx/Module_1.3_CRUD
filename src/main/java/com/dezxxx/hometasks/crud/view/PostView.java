package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.PostController;
import com.dezxxx.hometasks.crud.model.Post;

import java.util.List;
import java.util.Scanner;

public class PostView {
    private final PostController controller;
    private final Scanner scanner;

    public PostView(PostController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void menu() {
        while (true) {
            System.out.println("\n--- POST MENU ---");
            System.out.println("1. Create");
            System.out.println("2. Get all");
            System.out.println("3. Get by id");
            System.out.println("4. Get by writer id");
            System.out.println("5. Update");
            System.out.println("6. Delete");
            System.out.println("0. Back");
            System.out.print("Choose: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> create();
                    case "2" -> getAll();
                    case "3" -> getById();
                    case "4" -> getByWriterId();
                    case "5" -> update();
                    case "6" -> delete();
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
        Long writerId = InputUtil.readLong(scanner, "Enter writerId: ");
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        System.out.print("Enter content: ");
        String content = scanner.nextLine();
        List<Long> labelIds = InputUtil.readLongList(scanner, "Enter label ids (comma separated, or empty): ");

        Post created = controller.create(writerId, title, content, labelIds);
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

    private void getById() {
        Long id = InputUtil.readLong(scanner, "Enter id: ");
        System.out.println(controller.getById(id));
    }

    private void getByWriterId() {
        Long writerId = InputUtil.readLong(scanner, "Enter writerId: ");
        List<Post> posts = controller.getByWriterId(writerId);
        if (posts.isEmpty()) {
            System.out.println("No posts for writerId=" + writerId);
            return;
        }
        posts.forEach(System.out::println);
    }

    private void update() {
        Long postId = InputUtil.readLong(scanner, "Enter postId: ");
        System.out.print("Enter new title: ");
        String title = scanner.nextLine();
        System.out.print("Enter new content: ");
        String content = scanner.nextLine();
        List<Long> labelIds = InputUtil.readLongList(scanner, "Enter label ids (comma separated, or empty): ");

        System.out.println("Updated: " + controller.update(postId, title, content, labelIds));
    }

    private void delete() {
        Long postId = InputUtil.readLong(scanner, "Enter postId: ");
        controller.delete(postId);
        System.out.println("Deleted (soft)");
    }
}