package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.config.PostStatus;
import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.controller.PostController;
import com.dezxxx.hometasks.crud.controller.WriterController;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.util.InputUtil;
import com.dezxxx.hometasks.crud.util.Pager;
import com.dezxxx.hometasks.crud.util.UserCancelledException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PostView {

    private final PostController postController;
    private final LabelController labelController;
    private final WriterController writerController;
    private final Scanner scanner;

    public PostView(PostController postController,
                    LabelController labelController,
                    WriterController writerController,
                    Scanner scanner) {
        this.postController = postController;
        this.labelController = labelController;
        this.writerController = writerController;
        this.scanner = scanner;
    }

    public void menu() {

        while (true) {

            System.out.println("\n--- POST MENU ---");
            System.out.println("1. Create");
            System.out.println("2. Get all");
            System.out.println("3. Update");
            System.out.println("4. Delete");
            System.out.println("5. Change status");
            System.out.println("0. Back");

            int choice = InputUtil.readChoice(scanner, "Choose: ");

            boolean needsPause = true;

            try {

                switch (choice) {
                    case 1 -> create();
                    case 2 -> { getAll(); needsPause = false; }
                    case 3 -> update();
                    case 4 -> delete();
                    case 5 -> changeStatus();
                    case 0 -> { return; }
                    default -> { System.out.println("Invalid option. Try again."); needsPause = false; }
                }

            } catch (UserCancelledException e) {
                System.out.println("Cancelled. Returning to Post menu.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (needsPause) pause();
        }
    }

    private void create() {

        String title   = InputUtil.readText(scanner, "Title   (min 3 chars): ", 3);
        String content = InputUtil.readText(scanner, "Content (min 3 chars): ", 3);

        Writer writer = chooseWriter();

        List<Label> labels = chooseLabels();

        Post created = postController.create(title, content, labels, writer);
        System.out.printf("Created: [ID: %d] \"%s\" [%s]%n",
                created.getId(), created.getTitle(), created.getStatus());
    }

    private void getAll() {

        List<Post> all = postController.getAll();

        if (all.isEmpty()) {
            System.out.println("No active posts found.");
            return;
        }

        System.out.print("Search by title (Enter to show all): ");
        String query = scanner.nextLine().trim().toLowerCase();

        List<Post> filtered = query.isEmpty() ? all :
                all.stream()
                        .filter(p -> p.getTitle().toLowerCase().contains(query))
                        .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            System.out.println("No posts match \"" + query + "\".");
            return;
        }

        Pager<Post> pager = new Pager<>(filtered);

        while (true) {

            System.out.println("\n=== Posts [" + filtered.size() +
                    " total, page " + pager.pageNumber() + "/" + pager.totalPages() + "] ===");

            pager.currentItems().forEach(this::printPost);

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

        Post post = choosePost("Choose post to update:");

        System.out.println("(Press Enter to keep current value)");

        String title = readUpdatedText(
                "Title   [" + post.getTitle() + "]: ",
                post.getTitle()
        );

        String content = readUpdatedText(
                "Content [" + post.getContent() + "]: ",
                post.getContent()
        );

        System.out.println("Labels (select again or 0 to keep current):");
        List<Label> labels = chooseLabels();
        if (labels.isEmpty()) {
            labels = post.getLabels();
        }

        Post updated = postController.update(post.getId(), title, content, labels);
        System.out.printf("Updated: [ID: %d] \"%s\"%n", updated.getId(), updated.getTitle());
    }

    private void delete() {

        Post post = choosePost("Choose post to delete:");

        boolean confirmed = InputUtil.readConfirmation(
                scanner,
                "Soft-delete post \"" + post.getTitle() + "\"? (status → DELETED)"
        );

        if (!confirmed) {
            System.out.println("Cancelled.");
            return;
        }

        postController.delete(post.getId());
        System.out.println("Post moved to DELETED.");
    }

    private void changeStatus() {

        Post post = choosePost("Choose post to change status:");

        System.out.println("Current status: [" + post.getStatus() + "]");
        System.out.println("\nNew status:");
        System.out.println("1. ACTIVE");
        System.out.println("2. UNDER_REVIEW");
        System.out.println("0. Cancel");

        int choice = InputUtil.readChoice(scanner, "Choose: ");

        PostStatus newStatus = switch (choice) {
            case 1 -> PostStatus.ACTIVE;
            case 2 -> PostStatus.UNDER_REVIEW;
            case 0 -> throw new UserCancelledException();
            default -> throw new IllegalArgumentException("Invalid status option.");
        };

        Post updated = postController.changeStatus(post.getId(), newStatus);
        System.out.println("Status changed to: [" + updated.getStatus() + "]");
    }

    private Writer chooseWriter() {

        List<Writer> all = writerController.getAll();

        if (all.isEmpty()) {
            throw new IllegalStateException("No writers found.");
        }

        Pager<Writer> pager = new Pager<>(all);

        while (true) {

            System.out.println("\nChoose writer (page " +
                    pager.pageNumber() + "/" + pager.totalPages() + "):");

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

    private Post choosePost(String title) {

        List<Post> all = postController.getAll();

        if (all.isEmpty()) {
            throw new IllegalStateException("No active posts found.");
        }

        Pager<Post> pager = new Pager<>(all);

        while (true) {

            System.out.println("\n" + title +
                    " (page " + pager.pageNumber() + "/" + pager.totalPages() + ")");

            List<Post> page = pager.currentItems();

            for (int i = 0; i < page.size(); i++) {
                Post p = page.get(i);
                System.out.printf("  %d. [ID: %d] \"%s\" [%s]%n",
                        i + 1, p.getId(), p.getTitle(), p.getStatus());
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

    private List<Label> chooseLabels() {

        List<Label> all = labelController.getAll();
        List<Label> selected = new ArrayList<>();

        if (all.isEmpty()) {
            System.out.println("No labels available.");
            return selected;
        }

        System.out.println("\nSelect labels (0 to finish):");

        while (true) {

            for (int i = 0; i < all.size(); i++) {
                Label current = all.get(i);
                boolean isSelected = selected.stream()
                        .anyMatch(l -> l.getId().equals(current.getId()));
                System.out.printf("  %d. %s%s%n",
                        i + 1, current.getName(), isSelected ? " ✓" : "");
            }

            System.out.println("  0 → done");

            int choice = InputUtil.readChoice(scanner, "Choose label: ");

            if (choice == 0) break;

            if (choice < 1 || choice > all.size()) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            Label label = all.get(choice - 1);

            boolean alreadyAdded = selected.stream()
                    .anyMatch(l -> l.getId().equals(label.getId()));

            if (alreadyAdded) {
                selected.removeIf(l -> l.getId().equals(label.getId()));
                System.out.println("Removed: " + label.getName());
            } else {
                selected.add(label);
                System.out.println("Added: " + label.getName());
            }
        }

        return selected;
    }

    private void printPost(Post post) {

        System.out.printf("%n  [ID: %d] \"%s\" [%s]%n",
                post.getId(), post.getTitle(), post.getStatus());

        System.out.println("  Content: " + post.getContent());

        if (post.getWriter() != null) {
            System.out.printf("  Author:  [ID: %d] %s %s%n",
                    post.getWriter().getId(),
                    post.getWriter().getFirstName(),
                    post.getWriter().getLastName());
        }

        if (post.getLabels() != null && !post.getLabels().isEmpty()) {
            String labels = post.getLabels().stream()
                    .map(Label::getName)
                    .collect(Collectors.joining(", "));
            System.out.println("  Labels:  " + labels);
        }

        System.out.println("  Created: " + post.getCreated() +
                "  Updated: " + post.getUpdated());
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
