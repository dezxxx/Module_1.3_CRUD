package com.dezxxx.hometasks.crud.repository.impl;

import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class GsonPostRepositoryImpl implements PostRepository {

    private static final Type LIST_TYPE = new TypeToken<List<Post>>() {}.getType();
    private final Path storagePath;

    public GsonPostRepositoryImpl(Path storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public Post save(Post entity) {
        List<Post> posts = read();

        entity.setId(generateId(posts));
        if (entity.getStatus() == null) {
            entity.setStatus(Status.ACTIVE);
        }

        posts.add(entity);
        write(posts);
        return entity;
    }

    @Override
    public Post update(Post entity) {
        validateId(entity.getId());

        List<Post> posts = read();
        int index = findIndex(posts, entity.getId());

        if (index == -1) {
            throw new NoSuchElementException("Post not found: id=" + entity.getId());
        }

        if (entity.getStatus() == null) {
            entity.setStatus(posts.get(index).getStatus());
        }

        posts.set(index, entity);
        write(posts);
        return entity;
    }

    @Override
    public Optional<Post> findById(Long id) {
        return read().stream()
                .filter(post -> Objects.equals(post.getId(), id))
                .filter(post -> post.getStatus() == Status.ACTIVE)
                .findFirst();
    }

    @Override
    public List<Post> findAll() {
        return read().stream()
                .filter(post -> post.getStatus() == Status.ACTIVE)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        List<Post> posts = read();

        boolean changed = posts.stream()
                .filter(post -> Objects.equals(post.getId(), id))
                .findFirst()
                .map(post -> {
                    post.setStatus(Status.DELETED);
                    return true;
                })
                .orElse(false);

        if (!changed) {
            throw new NoSuchElementException("Post not found: id=" + id);
        }

        write(posts);
    }

    private List<Post> read() {
        return JsonUtil.readList(storagePath, LIST_TYPE);
    }

    private void write(List<Post> posts) {
        JsonUtil.writeList(storagePath, posts, LIST_TYPE);
    }

    private Long generateId(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
    }

    private int findIndex(List<Post> posts, Long id) {
        for (int i = 0; i < posts.size(); i++) {
            if (Objects.equals(posts.get(i).getId(), id)) {
                return i;
            }
        }
        return -1;
    }
}
