package com.dezxxx.hometasks.crud.repository.impl;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class LabelJsonRepository implements LabelRepository {

    private static final Type LIST_TYPE =
            new TypeToken<List<Label>>() {}.getType();

    private final Path storagePath;

    public LabelJsonRepository(Path storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public Label save(Label entity) {
        List<Label> labels = JsonUtil.readList(storagePath, LIST_TYPE);

        long nextId = labels.stream()
                .map(Label::getId)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L) + 1;

        entity.setId(nextId);

        if (entity.getStatus() == null) {
            entity.setStatus(Status.ACTIVE);
        }

        labels.add(entity);
        JsonUtil.writeList(storagePath, labels, LIST_TYPE);

        return entity;
    }

    @Override
    public Label update(Label entity) {
        if (entity.getId() == null) {
            throw new IllegalArgumentException("Label id must not be null");
        }

        List<Label> labels = JsonUtil.readList(storagePath, LIST_TYPE);

        int index = -1;
        for (int i = 0; i < labels.size(); i++) {
            if (Objects.equals(labels.get(i).getId(), entity.getId())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new NoSuchElementException("Label not found: id=" + entity.getId());
        }

        // если статус не передали — сохраняем старый
        if (entity.getStatus() == null) {
            entity.setStatus(labels.get(index).getStatus());
        }

        labels.set(index, entity);
        JsonUtil.writeList(storagePath, labels, LIST_TYPE);

        return entity;
    }

    @Override
    public Optional<Label> findById(Long id) {
        List<Label> labels = JsonUtil.readList(storagePath, LIST_TYPE);

        return labels.stream()
                .filter(l -> Objects.equals(l.getId(), id))
                .filter(l -> l.getStatus() == Status.ACTIVE)
                .findFirst();
    }

    @Override
    public List<Label> findAll() {
        List<Label> labels = JsonUtil.readList(storagePath, LIST_TYPE);

        return labels.stream()
                .filter(l -> l.getStatus() == Status.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        List<Label> labels = JsonUtil.readList(storagePath, LIST_TYPE);

        boolean changed = labels.stream()
                .filter(l -> Objects.equals(l.getId(), id))
                .findFirst()
                .map(l -> {
                    l.setStatus(Status.DELETED);
                    return true;
                })
                .orElse(false);

        if (!changed) {
            throw new NoSuchElementException("Label not found: id=" + id);
        }

        JsonUtil.writeList(storagePath, labels, LIST_TYPE);
    }
}