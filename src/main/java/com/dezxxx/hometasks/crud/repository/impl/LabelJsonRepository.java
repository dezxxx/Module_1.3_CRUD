package com.dezxxx.hometasks.crud.repository.impl;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class LabelJsonRepository implements LabelRepository {

    private static final Type LIST_TYPE = new TypeToken<List<Label>>() {}.getType();
    private final Path storagePath;

    public LabelJsonRepository(Path storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public Label save(Label entity) {
        List<Label> labels = read();

        entity.setId(generateId(labels));
        if (entity.getStatus() == null) {
            entity.setStatus(Status.ACTIVE);
        }

        labels.add(entity);
        write(labels);
        return entity;
    }

    @Override
    public Label update(Label entity) {
        validateId(entity.getId());

        List<Label> labels = read();
        int index = findIndex(labels, entity.getId());

        if (index == -1) {
            throw new NoSuchElementException("Label not found: id=" + entity.getId());
        }

        if (entity.getStatus() == null) {
            entity.setStatus(labels.get(index).getStatus());
        }

        labels.set(index, entity);
        write(labels);
        return entity;
    }

    @Override
    public Optional<Label> findById(Long id) {
        return read().stream()
                .filter(label -> Objects.equals(label.getId(), id))
                .filter(label -> label.getStatus() == Status.ACTIVE)
                .findFirst();
    }

    @Override
    public List<Label> findAll() {
        return read().stream()
                .filter(label -> label.getStatus() == Status.ACTIVE)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        List<Label> labels = read();

        boolean changed = labels.stream()
                .filter(label -> Objects.equals(label.getId(), id))
                .findFirst()
                .map(label -> {
                    label.setStatus(Status.DELETED);
                    return true;
                })
                .orElse(false);

        if (!changed) {
            throw new NoSuchElementException("Label not found: id=" + id);
        }

        write(labels);
    }

    private List<Label> read() {
        return JsonUtil.readList(storagePath, LIST_TYPE);
    }

    private void write(List<Label> labels) {
        JsonUtil.writeList(storagePath, labels, LIST_TYPE);
    }

    private Long generateId(List<Label> labels) {
        return labels.stream()
                .map(Label::getId)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
    }

    private int findIndex(List<Label> labels, Long id) {
        for (int i = 0; i < labels.size(); i++) {
            if (Objects.equals(labels.get(i).getId(), id)) {
                return i;
            }
        }
        return -1;
    }
}
