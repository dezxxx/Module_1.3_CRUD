package com.dezxxx.hometasks.crud.view;

import com.dezxxx.hometasks.crud.util.InputUtil;

import java.util.Scanner;

public class MainView {

    private final WriterView writerView;
    private final PostView postView;
    private final LabelView labelView;
    private final Scanner scanner;

    public MainView(WriterView writerView, PostView postView, LabelView labelView, Scanner scanner) {
        this.writerView = writerView;
        this.postView = postView;
        this.labelView = labelView;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Writers");
            System.out.println("2. Posts");
            System.out.println("3. Labels");
            System.out.println("0. Exit");

            int choice = InputUtil.readChoice(scanner, "Choose: ");

            switch (choice) {
                case 1 -> writerView.menu();
                case 2 -> postView.menu();
                case 3 -> labelView.menu();
                case 0 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid option");
            }
        }
    }
}
