package com.dezxxx.hometasks.crud;

import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.controller.PostController;
import com.dezxxx.hometasks.crud.controller.WriterController;

import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.repository.WriterRepository;

import com.dezxxx.hometasks.crud.repository.impl.jdbc.JdbcLabelRepositoryImpl;
import com.dezxxx.hometasks.crud.repository.impl.jdbc.JdbcPostRepositoryImpl;
import com.dezxxx.hometasks.crud.repository.impl.jdbc.JdbcWriterRepositoryImpl;

import com.dezxxx.hometasks.crud.service.LabelService;
import com.dezxxx.hometasks.crud.service.PostService;
import com.dezxxx.hometasks.crud.service.WriterService;

import com.dezxxx.hometasks.crud.util.ConnectionManager;
import com.dezxxx.hometasks.crud.util.LiquibaseMigration;

import com.dezxxx.hometasks.crud.validation.CompositeStrategy;
import com.dezxxx.hometasks.crud.validation.NotBlankStrategy;
import com.dezxxx.hometasks.crud.validation.PositiveIdStrategy;
import com.dezxxx.hometasks.crud.validation.ValidationStrategy;

import com.dezxxx.hometasks.crud.view.LabelView;
import com.dezxxx.hometasks.crud.view.MainView;
import com.dezxxx.hometasks.crud.view.PostView;
import com.dezxxx.hometasks.crud.view.WriterView;

import java.sql.Connection;
import java.util.Scanner;

// Facade: скрывает всю сложность инициализации приложения.
// App.java знает только об этом классе и вызывает start().
public class ApplicationContext {

    private final MainView mainView;

    public ApplicationContext() {

        try {

            // =========================
            // 1. CONNECTION  (Singleton)
            // =========================
            Connection connection =
                    ConnectionManager.getInstance().getConnection();

            // =========================
            // 2. LIQUIBASE
            // =========================
            LiquibaseMigration.run(connection);
            connection.setAutoCommit(true);

            // =========================
            // 3. VALIDATORS  (Strategy)
            // =========================
            ValidationStrategy<String> textValidator =
                    new CompositeStrategy<>(new NotBlankStrategy());
            ValidationStrategy<Long> idValidator =
                    new CompositeStrategy<>(new PositiveIdStrategy());

            // =========================
            // 4. REPOSITORIES
            // =========================
            LabelRepository labelRepository =
                    new JdbcLabelRepositoryImpl(connection);

            PostRepository postRepository =
                    new JdbcPostRepositoryImpl(connection);

            WriterRepository writerRepository =
                    new JdbcWriterRepositoryImpl(connection);

            // =========================
            // 5. SERVICES
            // =========================
            LabelService labelService =
                    new LabelService(labelRepository, textValidator, idValidator);

            PostService postService =
                    new PostService(postRepository, textValidator, idValidator);

            WriterService writerService =
                    new WriterService(writerRepository, textValidator, idValidator);

            // =========================
            // 6. CONTROLLERS
            // =========================
            LabelController labelController =
                    new LabelController(labelService);

            PostController postController =
                    new PostController(postService);

            WriterController writerController =
                    new WriterController(writerService);

            // =========================
            // 7. SCANNER
            // =========================
            Scanner scanner = new Scanner(System.in);

            // =========================
            // 8. VIEWS
            // =========================
            LabelView labelView =
                    new LabelView(labelController, scanner);

            PostView postView =
                    new PostView(
                            postController,
                            labelController,
                            writerController,
                            scanner
                    );

            WriterView writerView =
                    new WriterView(writerController, scanner);

            // =========================
            // 9. MAIN VIEW
            // =========================
            this.mainView =
                    new MainView(writerView, postView, labelView, scanner);

        } catch (Exception e) {

            System.out.println("======================================");
            System.out.println("  Application failed to start.");
            System.out.println("  Make sure the database is running");
            System.out.println("  and the connection settings are correct.");
            System.out.println("======================================");
            System.exit(1);
            throw new IllegalStateException();
        }
    }

    public void start() {
        mainView.start();
    }
}
