package com.dezxxx.hometasks.crud.service;

import com.dezxxx.hometasks.crud.config.PostStatus;
import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.validation.ValidationStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

public class PostService {

    private final PostRepository repository;
    private final ValidationStrategy<String> textValidator;
    private final ValidationStrategy<Long> idValidator;

    public PostService(PostRepository repository,
                       ValidationStrategy<String> textValidator,
                       ValidationStrategy<Long> idValidator) {
        this.repository = repository;
        this.textValidator = textValidator;
        this.idValidator = idValidator;
    }

    public Post create(String title,
                       String content,
                       List<Label> labels,
                       Writer writer) {

        textValidator.validate(title, "title");
        textValidator.validate(content, "content");

        Post post = new Post();
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setCreated(LocalDateTime.now());
        post.setUpdated(LocalDateTime.now());
        post.setStatus(PostStatus.ACTIVE.name());
        post.setLabels(labels);
        post.setWriter(writer);

        return repository.save(post);
    }

    public List<Post> getAll() {
        return repository.findAll();
    }

    public Post getById(Long id) {

        idValidator.validate(id, "id");

        return repository.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Post not found: " + id
                        )
                );
    }

    public Post update(Long id,
                       String title,
                       String content,
                       List<Label> labels) {

        idValidator.validate(id, "id");
        textValidator.validate(title, "title");
        textValidator.validate(content, "content");

        Post post = getById(id);
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setUpdated(LocalDateTime.now());
        post.setLabels(labels);

        return repository.update(post);
    }

    public void delete(Long id) {

        idValidator.validate(id, "id");

        repository.deleteById(id);
    }

    public Post changeStatus(Long id, PostStatus status) {

        idValidator.validate(id, "id");

        Post post = getById(id);
        post.setStatus(status.name());
        post.setUpdated(LocalDateTime.now());

        return repository.update(post);
    }
}
