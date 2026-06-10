package com.dezxxx.hometasks.crud;

import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.controller.PostController;
import com.dezxxx.hometasks.crud.controller.WriterController;

import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.repository.WriterRepository;

import com.dezxxx.hometasks.crud.repository.impl.hibernate.HibernateLabelRepositoryImpl;
import com.dezxxx.hometasks.crud.repository.impl.hibernate.HibernatePostRepositoryImpl;
import com.dezxxx.hometasks.crud.repository.impl.hibernate.HibernateWriterRepositoryImpl;

import com.dezxxx.hometasks.crud.service.LabelService;
import com.dezxxx.hometasks.crud.service.PostService;
import com.dezxxx.hometasks.crud.service.WriterService;

import com.dezxxx.hometasks.crud.util.DatabaseType;
import com.dezxxx.hometasks.crud.util.FlywayMigration;
import com.dezxxx.hometasks.crud.util.HibernateUtil;

import com.dezxxx.hometasks.crud.validation.CompositeStrategy;
import com.dezxxx.hometasks.crud.validation.NotBlankStrategy;
import com.dezxxx.hometasks.crud.validation.PositiveIdStrategy;
import com.dezxxx.hometasks.crud.validation.ValidationStrategy;

import com.dezxxx.hometasks.crud.view.DbSelectionView;
import com.dezxxx.hometasks.crud.view.LabelView;
import com.dezxxx.hometasks.crud.view.MainView;
import com.dezxxx.hometasks.crud.view.PostView;
import com.dezxxx.hometasks.crud.view.WriterView;

import java.util.Scanner;

// Facade: скрывает всю сложность инициализации приложения.
// App.java знает только об этом классе и вызывает start().
public class ApplicationContext {

    private final MainView mainView;

    public ApplicationContext() {

        try {

            // =========================
            // 1. SCANNER
            // =========================
            Scanner scanner = new Scanner(System.in);

            // =========================
            // 2. DB SELECTION
            // =========================
            DatabaseType dbType = new DbSelectionView(scanner).select();

            // =========================
            // 3. FLYWAY MIGRATIONS
            // =========================
            FlywayMigration.run(dbType);

            // =========================
            // 4. HIBERNATE INIT
            // =========================
            HibernateUtil.init(dbType);

            // =========================
            // 5. VALIDATORS  (Strategy)
            // =========================
            ValidationStrategy<String> textValidator =
                    new CompositeStrategy<>(new NotBlankStrategy());
            ValidationStrategy<Long> idValidator =
                    new CompositeStrategy<>(new PositiveIdStrategy());

            // =========================
            // 6. REPOSITORIES
            // =========================
            LabelRepository labelRepository =
                    new HibernateLabelRepositoryImpl();

            PostRepository postRepository =
                    new HibernatePostRepositoryImpl();

            WriterRepository writerRepository =
                    new HibernateWriterRepositoryImpl();

            // =========================
            // 7. SERVICES
            // =========================
            LabelService labelService =
                    new LabelService(labelRepository, textValidator, idValidator);

            PostService postService =
                    new PostService(postRepository, textValidator, idValidator);

            WriterService writerService =
                    new WriterService(writerRepository, textValidator, idValidator);

            // =========================
            // 8. CONTROLLERS
            // =========================
            LabelController labelController =
                    new LabelController(labelService);

            PostController postController =
                    new PostController(postService);

            WriterController writerController =
                    new WriterController(writerService);

            // =========================
            // 9. VIEWS
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
            // 10. MAIN VIEW
            // =========================
            this.mainView =
                    new MainView(writerView, postView, labelView, scanner);

        } catch (Exception e) {

            System.out.println("======================================");
            System.out.println("  Application failed to start.");
            System.out.println("  Cause: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("  Root:  " + e.getCause().getMessage());
            }
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