package com.dezxxx.hometasks.crud;

import com.dezxxx.hometasks.crud.controller.*;
import com.dezxxx.hometasks.crud.repository.*;
import com.dezxxx.hometasks.crud.repository.impl.*;
import com.dezxxx.hometasks.crud.view.*;

import java.nio.file.Path;
import java.util.Scanner;

public class ApplicationContext {

    private final MainView mainView;

    public ApplicationContext() {

        Path labelsPath = Path.of("data", "labels.json");
        Path postsPath  = Path.of("data", "posts.json");
        Path writersPath= Path.of("data", "writers.json");

        LabelRepository labelRepo = new LabelJsonRepository(labelsPath);
        PostRepository postRepo = new GsonPostRepositoryImpl(postsPath, labelRepo);
        WriterRepository writerRepo = new GsonWriterRepositoryImpl(writersPath, postRepo);

        LabelController labelController = new LabelController(labelRepo);
        WriterController writerController = new WriterController(writerRepo);
        PostController postController = new PostController(postRepo, writerRepo, labelRepo);

        Scanner scanner = new Scanner(System.in);

        LabelView labelView = new LabelView(labelController, scanner);
        WriterView writerView = new WriterView(writerController, scanner);
        PostView postView = new PostView(postController, scanner);

        this.mainView = new MainView(writerView, postView, labelView, scanner);
    }

    public void start() {
        mainView.start();
    }
}