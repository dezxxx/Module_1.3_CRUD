package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.repository.WriterRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class PostController {

    private final PostRepository postRepository;
    private final WriterRepository writerRepository;
    private final LabelRepository labelRepository;


    public PostController(PostRepository postRepository,
                          WriterRepository writerRepository,
                          LabelRepository labelRepository) {
        this.postRepository = postRepository;
        this.writerRepository = writerRepository;
        this.labelRepository = labelRepository;
    }

    public Post create(Long writerId, String title, String content, List<Long> labelIds) {
        validateId(writerId);
        validateTitle(title);
        validateContent(content);

        // writer должен существовать и быть ACTIVE
        writerRepository.findById(writerId)
                .orElseThrow(() -> new NoSuchElementException("Writer not found or DELETED: id=" + writerId));

        Post post = new Post();
        post.setWriterId(writerId);
        post.setTitle(clean(title));        //  сразу clean
        post.setContent(clean(content));    // сразу clean
        post.setStatus(Status.ACTIVE);

        List<Label> labels = resolveActiveLabels(labelIds);
        post.setLabels(labels);

        Post saved = postRepository.save(post);

        // привязать postId к writer (WriterRecord хранит postIds)
        // делаем через writerRepository update
        var writer = writerRepository.findById(writerId).orElseThrow();
        writer.getPosts().add(saved); // posts — это модельная коллекция, репо сохранит как postIds
        writerRepository.update(writer);

        return saved;
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    public List<Post> getByWriterId(Long writerId) {
        validateId(writerId);
        return postRepository.findByWriterId(writerId);
    }

    public Post getById(Long id) {
        validateId(id);
        return postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found: id=" + id));
    }

    public Post update(Long postId, String title, String content, List<Long> labelIds) {
        validateId(postId);
        validateTitle(title);
        validateContent(content);

        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: id=" + postId));

        existing.setTitle(clean(title));
        existing.setContent(clean(content));
        existing.setLabels(resolveActiveLabels(labelIds));

        return postRepository.update(existing);


    }

    public void delete(Long postId) {
        validateId(postId);

        Post existing = postRepository.findById(postId)
                .orElseThrow(() -> new NoSuchElementException("Post not found: id=" + postId));

        postRepository.deleteById(postId);

        // убрать postId из writer.postIds (через update writer)
        Long writerId = existing.getWriterId();
        if (writerId != null) {
            writerRepository.findById(writerId).ifPresent(writer -> {
                writer.setPosts(
                        writer.getPosts().stream()
                                .filter(p -> p.getId() != null && !p.getId().equals(postId))
                                .collect(Collectors.toList())
                );
                writerRepository.update(writer);
            });
        }
    }

    private List<Label> resolveActiveLabels(List<Long> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) return List.of();

        return labelIds.stream()
                .distinct()
                .map(id -> labelRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Label not found or DELETED: id=" + id)))
                .collect(Collectors.toList());
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Id must be positive");
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title must not be blank");
    }

    private void validateContent(String content) {
        if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("Content must not be blank");
    }
    private String clean(String s) {
        String t = s.trim();
        while (t.endsWith("\\")) {
            t = t.substring(0, t.length() - 1).trim();
        }
        return t;
    }
}

