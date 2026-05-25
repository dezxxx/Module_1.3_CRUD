package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.WriterController;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.util.InputUtil;
import com.dezxxx.hometasks.crud.util.Pager;
import com.dezxxx.hometasks.crud.util.RepositoryException;
import com.dezxxx.hometasks.crud.util.UserCancelledException;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class WriterView {

    private final WriterController writerController;
    private final Scanner scanner;

    public WriterView(WriterController writerController, Scanner scanner) {
        this.writerController = writerController;
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

            boolean needsPause = true;

            try {

                switch (choice) {
                    case 1 -> create();
                    case 2 -> { getAll(); needsPause = false; }
                    case 3 -> update();
                    case 4 -> delete();
                    case 0 -> { return; }
                    default -> { System.out.println("Invalid option. Try again."); needsPause = false; }
                }

            } catch (UserCancelledException e) {
                System.out.println("Cancelled. Returning to Writer menu.");
            } catch (RepositoryException e) {
                System.out.println("Database error: " + e.getMessage());
            } catch (Exception e) {
                String msg = e.getMessage();
                System.out.println("Error: " + (msg != null ? msg : e.getClass().getSimpleName()));
            }

            if (needsPause) pause();
        }
    }

    private void create() {

        String firstName = InputUtil.readText(scanner, "First name (min 2 chars): ", 2);
        String lastName  = InputUtil.readText(scanner, "Last name  (min 2 chars): ", 2);

        Writer created = writerController.create(firstName, lastName);
        System.out.println("Created: [ID: " + created.getId() + "] " +
                created.getFirstName() + " " + created.getLastName());
    }

    private void getAll() {

        List<Writer> all = writerController.getAll();

        if (all.isEmpty()) {
            System.out.println("No writers found.");
            return;
        }

        System.out.print("Search by name (Enter to show all): ");
        String query = scanner.nextLine().trim().toLowerCase();

        List<Writer> filtered = query.isEmpty() ? all :
                all.stream()
                        .filter(w -> (w.getFirstName() + " " + w.getLastName())
                                .toLowerCase().contains(query))
                        .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No writers match \"" + query + "\".");
            return;
        }

        Pager<Writer> pager = new Pager<>(filtered);

        while (true) {

            System.out.println("\n=== Writers [" + filtered.size() +
                    " total, page " + pager.pageNumber() + "/" + pager.totalPages() + "] ===");

            pager.currentItems().forEach(this::printWriter);

            if (pager.isSinglePage()) break;

            System.out.println("---");
            if (pager.hasNext()) System.out.println("n → next page");
            if (pager.hasPrev()) System.out.println("p ← prev page");
            System.out.println("0 → back");

            int input = InputUtil.readPagedInput(scanner);

            if (input == 0)                  break;
            if (input == Integer.MAX_VALUE)  pager.next();
            if (input == Integer.MIN_VALUE)  pager.prev();
        }
    }

    private void update() {

        Writer writer = chooseWriter("Choose writer to update:");

        System.out.println("(Press Enter to keep current value)");

        String firstName = readUpdatedText(
                "First name [" + writer.getFirstName() + "]: ",
                writer.getFirstName()
        );

        String lastName = readUpdatedText(
                "Last name  [" + writer.getLastName() + "]: ",
                writer.getLastName()
        );

        Writer updated = writerController.update(writer.getId(), firstName, lastName);
        System.out.println("Updated: [ID: " + updated.getId() + "] " +
                updated.getFirstName() + " " + updated.getLastName());
    }

    private void delete() {

        Writer writer = chooseWriter("Choose writer to delete:");

        boolean confirmed = InputUtil.readConfirmation(
                scanner,
                "Delete \"" + writer.getFirstName() + " " + writer.getLastName() + "\"?"
        );

        if (!confirmed) {
            System.out.println("Cancelled.");
            return;
        }

        writerController.delete(writer.getId());
        System.out.println("Writer deleted.");
    }

    Writer chooseWriter(String title) {

        List<Writer> all = writerController.getAll();

        if (all.isEmpty()) {
            throw new IllegalStateException("No writers found.");
        }

        Pager<Writer> pager = new Pager<>(all);

        while (true) {

            System.out.println("\n" + title +
                    " (page " + pager.pageNumber() + "/" + pager.totalPages() + ")");

            List<Writer> page = pager.currentItems();

            for (int i = 0; i < page.size(); i++) {
                Writer w = page.get(i);
                System.out.printf("  %d. [ID: %d] %s %s%n",
                        i + 1, w.getId(), w.getFirstName(), w.getLastName());
            }

            System.out.println("---");
            if (pager.hasNext()) System.out.println("  n → next page");
            if (pager.hasPrev()) System.out.println("  p ← prev page");
            System.out.println("  0 → cancel");

            int input = InputUtil.readPagedInput(scanner);

            if (input == 0)                 throw new UserCancelledException();
            if (input == Integer.MAX_VALUE) { pager.next(); continue; }
            if (input == Integer.MIN_VALUE) { pager.prev(); continue; }

            if (input >= 1 && input <= page.size()) {
                return pager.getByPageIndex(input - 1);
            }

            System.out.println("Invalid choice. Try again.");
        }
    }

    private void printWriter(Writer writer) {

        System.out.println("\n  [ID: " + writer.getId() + "] " +
                writer.getFirstName() + " " + writer.getLastName());

        List<Post> posts = writer.getPosts();

        if (posts == null || posts.isEmpty()) {
            System.out.println("  Posts: none");
            return;
        }

        System.out.println("  Posts:");

        for (Post post : posts) {
            System.out.printf("    - [ID: %d] %s [%s]%n",
                    post.getId(), post.getTitle(), post.getStatus());
        }
    }

    private String readUpdatedText(String prompt, String currentValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? currentValue : input;
    }

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
