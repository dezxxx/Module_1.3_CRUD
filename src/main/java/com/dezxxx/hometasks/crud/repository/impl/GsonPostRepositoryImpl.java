package com.dezxxx.hometasks.crud.repository.impl;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GsonPostRepositoryImpl implements PostRepository {

    private static class PostRecord {
        Long id;
        Long writerId;
        String title;
        String content;
        Status status;
        List<Long> labelIds;
    }

    private static final Type LIST_TYPE = new TypeToken<List<PostRecord>>() {}.getType();

    private final Path storagePath;
    private final LabelRepository labelRepository;

    public GsonPostRepositoryImpl(Path storagePath, LabelRepository labelRepository) {
        this.storagePath = storagePath;
        this.labelRepository = labelRepository;
    }

    @Override
    public Post save(Post entity) {
        List<PostRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        long nextId = records.stream()
                .map(r -> r.id)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L) + 1;

        PostRecord r = toRecord(entity);
        r.id = nextId;
        if (r.status == null) r.status = Status.ACTIVE;

        records.add(r);
        JsonUtil.writeList(storagePath, records, LIST_TYPE);

        entity.setId(nextId);
        return entity;
    }

    @Override
    public Post update(Post entity) {
        if (entity.getId() == null) throw new IllegalArgumentException("Post id is null");

        List<PostRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        int idx = indexOfId(records, entity.getId());
        if (idx == -1) throw new NoSuchElementException("Post not found: id=" + entity.getId());

        PostRecord updated = toRecord(entity);
        if (updated.status == null) updated.status = records.get(idx).status;

        records.set(idx, updated);
        JsonUtil.writeList(storagePath, records, LIST_TYPE);

        return entity;
    }

    @Override
    public Optional<Post> findById(Long id) {
        List<PostRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        return records.stream()
                .filter(r -> Objects.equals(r.id, id))
                .filter(r -> r.status == Status.ACTIVE)
                .findFirst()
                .map(this::toModel);
    }

    @Override
    public List<Post> findAll() {
        List<PostRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        return records.stream()
                .filter(r -> r.status == Status.ACTIVE)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        List<PostRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        boolean changed = records.stream()
                .filter(r -> Objects.equals(r.id, id))
                .findFirst()
                .map(r -> { r.status = Status.DELETED; return true; })
                .orElse(false);

        if (!changed) throw new NoSuchElementException("Post not found: id=" + id);

        JsonUtil.writeList(storagePath, records, LIST_TYPE);
    }

    @Override
    public List<Post> findByWriterId(Long writerId) {
        List<PostRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        return records.stream()
                .filter(r -> Objects.equals(r.writerId, writerId))
                .filter(r -> r.status == Status.ACTIVE)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    // --- helpers ---
    private int indexOfId(List<PostRecord> records, Long id) {
        for (int i = 0; i < records.size(); i++) {
            if (Objects.equals(records.get(i).id, id)) return i;
        }
        return -1;
    }

    private PostRecord toRecord(Post post) {
        PostRecord r = new PostRecord();
        r.id = post.getId();
        r.writerId = post.getWriterId();
        r.title = post.getTitle();
        r.content = post.getContent();
        r.status = post.getStatus();

        r.labelIds = (post.getLabels() == null) ? new ArrayList<>() :
                post.getLabels().stream()
                        .map(Label::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        return r;
    }

    private Post toModel(PostRecord r) {
        Post post = new Post();
        post.setId(r.id);
        post.setWriterId(r.writerId);
        post.setTitle(r.title);
        post.setContent(r.content);
        post.setStatus(r.status);

        List<Label> labels = (r.labelIds == null) ? new ArrayList<>() :
                r.labelIds.stream()
                        .map(labelRepository::findById)
                        .flatMap(Optional::stream)
                        .collect(Collectors.toList());

        post.setLabels(labels);
        return post;
    }
}