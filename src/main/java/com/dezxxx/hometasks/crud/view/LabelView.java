package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.util.InputUtil;
import com.dezxxx.hometasks.crud.util.Pager;
import com.dezxxx.hometasks.crud.util.RepositoryException;
import com.dezxxx.hometasks.crud.util.UserCancelledException;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LabelView {

    private final LabelController labelController;
    private final Scanner scanner;

    public LabelView(LabelController labelController, Scanner scanner) {
        this.labelController = labelController;
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
                System.out.println("Cancelled. Returning to Label menu.");
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

        String name = InputUtil.readText(scanner, "Label name (min 2 chars): ", 2);

        Label created = labelController.create(name);
        System.out.println("Created: [ID: " + created.getId() + "] " + created.getName());
    }

    private void getAll() {

        List<Label> all = labelController.getAll();

        if (all.isEmpty()) {
            System.out.println("No labels found.");
            return;
        }

        System.out.print("Search by name (Enter to show all): ");
        String query = scanner.nextLine().trim().toLowerCase();

        List<Label> filtered = query.isEmpty() ? all :
                all.stream()
                        .filter(l -> l.getName().toLowerCase().contains(query))
                        .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No labels match \"" + query + "\".");
            return;
        }

        Pager<Label> pager = new Pager<>(filtered);

        while (true) {

            System.out.println("\n=== Labels [" + filtered.size() +
                    " total, page " + pager.pageNumber() + "/" + pager.totalPages() + "] ===");

            pager.currentItems().forEach(l ->
                    System.out.println("  [ID: " + l.getId() + "] " + l.getName()));

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

        Label label = chooseLabel("Choose label to update:");

        String name = InputUtil.readUpdatedText(
                scanner, "Name [" + label.getName() + "]",
                label.getName()
        );

        Label updated = labelController.update(label.getId(), name);
        System.out.println("Updated: [ID: " + updated.getId() + "] " + updated.getName());
    }

    private void delete() {

        Label label = chooseLabel("Choose label to delete:");

        boolean confirmed = InputUtil.readConfirmation(
                scanner,
                "Delete label \"" + label.getName() + "\"?"
        );

        if (!confirmed) {
            System.out.println("Cancelled.");
            return;
        }

        labelController.delete(label.getId());
        System.out.println("Label deleted.");
    }

    private Label chooseLabel(String title) {

        List<Label> all = labelController.getAll();

        if (all.isEmpty()) {
            throw new IllegalStateException("No labels found.");
        }

        Pager<Label> pager = new Pager<>(all);

        while (true) {

            System.out.println("\n" + title +
                    " (page " + pager.pageNumber() + "/" + pager.totalPages() + ")");

            List<Label> page = pager.currentItems();

            for (int i = 0; i < page.size(); i++) {
                Label l = page.get(i);
                System.out.printf("  %d. [ID: %d] %s%n", i + 1, l.getId(), l.getName());
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

    private void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}
