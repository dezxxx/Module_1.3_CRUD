package com.dezxxx.hometasks.crud.repository.impl;

import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.PostRepository;
import com.dezxxx.hometasks.crud.repository.WriterRepository;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class GsonWriterRepositoryImpl implements WriterRepository {

    private static class WriterRecord {
        Long id;
        String firstName;
        String lastName;
        Status status;
        List<Long> postIds;
    }

    private static final Type LIST_TYPE = new TypeToken<List<WriterRecord>>() {}.getType();

    private final Path storagePath;
    private final PostRepository postRepository;

    public GsonWriterRepositoryImpl(Path storagePath, PostRepository postRepository) {
        this.storagePath = storagePath;
        this.postRepository = postRepository;
    }

    @Override
    public Writer save(Writer entity) {
        List<WriterRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        long nextId = records.stream()
                .map(r -> r.id)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L) + 1;

        WriterRecord r = toRecord(entity);
        r.id = nextId;
        if (r.status == null) r.status = Status.ACTIVE;

        records.add(r);
        JsonUtil.writeList(storagePath, records, LIST_TYPE);

        entity.setId(nextId);
        return entity;
    }

    @Override
    public Writer update(Writer entity) {
        if (entity.getId() == null) throw new IllegalArgumentException("Writer id is null");

        List<WriterRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        int idx = indexOfId(records, entity.getId());
        if (idx == -1) throw new NoSuchElementException("Writer not found: id=" + entity.getId());

        WriterRecord updated = toRecord(entity);
        if (updated.status == null) updated.status = records.get(idx).status;

        records.set(idx, updated);
        JsonUtil.writeList(storagePath, records, LIST_TYPE);

        return entity;
    }

    @Override
    public Optional<Writer> findById(Long id) {
        List<WriterRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        return records.stream()
                .filter(r -> Objects.equals(r.id, id))
                .filter(r -> r.status == Status.ACTIVE)
                .findFirst()
                .map(this::toModel);
    }

    @Override
    public List<Writer> findAll() {
        List<WriterRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        return records.stream()
                .filter(r -> r.status == Status.ACTIVE)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        List<WriterRecord> records = JsonUtil.readList(storagePath, LIST_TYPE);

        boolean changed = records.stream()
                .filter(r -> Objects.equals(r.id, id))
                .findFirst()
                .map(r -> { r.status = Status.DELETED; return true; })
                .orElse(false);

        if (!changed) throw new NoSuchElementException("Writer not found: id=" + id);

        JsonUtil.writeList(storagePath, records, LIST_TYPE);
    }

    // --- helpers ---
    private int indexOfId(List<WriterRecord> records, Long id) {
        for (int i = 0; i < records.size(); i++) {
            if (Objects.equals(records.get(i).id, id)) return i;
        }
        return -1;
    }

    private WriterRecord toRecord(Writer writer) {
        WriterRecord r = new WriterRecord();
        r.id = writer.getId();
        r.firstName = writer.getFirstName();
        r.lastName = writer.getLastName();
        r.status = writer.getStatus();

        r.postIds = (writer.getPosts() == null) ? new ArrayList<>() :
                writer.getPosts().stream()
                        .map(Post::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        return r;
    }

    private Writer toModel(WriterRecord r) {
        Writer writer = new Writer();
        writer.setId(r.id);
        writer.setFirstName(r.firstName);
        writer.setLastName(r.lastName);
        writer.setStatus(r.status);

        List<Post> posts = (r.postIds == null) ? new ArrayList<>() :
                r.postIds.stream()
                        .map(postRepository::findById)
                        .flatMap(Optional::stream)
                        .collect(Collectors.toList());

        writer.setPosts(posts);
        return writer;
    }
}