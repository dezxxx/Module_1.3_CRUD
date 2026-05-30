package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.config.PostStatus;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.service.PostService;

import java.util.List;

public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    public Post create(String title,
                       String content,
                       List<Label> labels, Writer writer) {

        return postService.create(
                title,
                content,
                labels,
                writer
        );
    }

    public List<Post> getAll() {

        return postService.getAll();
    }

    public List<Post> getAllIncludingDeleted() {

        return postService.getAllIncludingDeleted();
    }

    public Post getById(Long id) {

        return postService.getById(id);
    }

    public Post update(Long id,
                       String title,
                       String content,
                       List<Label> labels) {

        return postService.update(
                id,
                title,
                content,
                labels
        );
    }

    public void delete(Long id) {

        postService.delete(id);
    }

    public Post changeStatus(Long id, PostStatus status) {

        return postService.changeStatus(id, status);
    }
}