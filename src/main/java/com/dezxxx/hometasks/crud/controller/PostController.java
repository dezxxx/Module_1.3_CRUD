package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.PostRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class PostController {

    private final PostRepository postRepository;

    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post create(String title, String content, List<Label> labels) {
        validateText(title, "title");
        validateText(content, "content");

        Post post = new Post();
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setStatus(Status.ACTIVE);
        post.setLabels(labels);

        return postRepository.save(post);
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public Post getById(Long id) {
        validateId(id);

        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found: id=" + id));
    }

    public Post update(Long id, String title, String content, List<Label> labels) {
        validateId(id);
        validateText(title, "title");
        validateText(content, "content");

        Post existing = getById(id);
        existing.setTitle(title.trim());
        existing.setContent(content.trim());
        existing.setLabels(labels);

        return postRepository.update(existing);
    }

    public void delete(Long id) {
        validateId(id);
        postRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be positive");
        }
    }

    private void validateText(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
