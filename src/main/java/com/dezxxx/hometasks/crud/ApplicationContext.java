package com.dezxxx.hometasks.crud;

import com.dezxxx.hometasks.crud.controller.LabelController;
import com.dezxxx.hometasks.crud.controller.PostController;
import com.dezxxx.hometasks.crud.controller.WriterController;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.repository.WriterRepository;
import com.dezxxx.hometasks.crud.repository.impl.GsonPostRepositoryImpl;
import com.dezxxx.hometasks.crud.repository.impl.GsonWriterRepositoryImpl;
import com.dezxxx.hometasks.crud.repository.impl.LabelJsonRepository;
import com.dezxxx.hometasks.crud.view.LabelView;
import com.dezxxx.hometasks.crud.view.MainView;
import com.dezxxx.hometasks.crud.view.PostView;
import com.dezxxx.hometasks.crud.view.WriterView;

import java.nio.file.Path;
import java.util.Scanner;

public class ApplicationContext {

    private final MainView mainView;

    public ApplicationContext() {
        Path labelsPath = Path.of("data", "labels.json");
        Path postsPath = Path.of("data", "posts.json");
        Path writersPath = Path.of("data", "writers.json");

        LabelRepository labelRepo = new LabelJsonRepository(labelsPath);
        PostRepository postRepo = new GsonPostRepositoryImpl(postsPath);
        WriterRepository writerRepo = new GsonWriterRepositoryImpl(writersPath);

        LabelController labelController = new LabelController(labelRepo);
        PostController postController = new PostController(postRepo);
        WriterController writerController = new WriterController(writerRepo);

        Scanner scanner = new Scanner(System.in);

        LabelView labelView = new LabelView(labelController, scanner);
        PostView postView = new PostView(postController, labelController, scanner);
        WriterView writerView = new WriterView(writerController, postController, scanner);

        this.mainView = new MainView(writerView, postView, labelView, scanner);
    }

    public void start() {
        mainView.start();
    }
}
