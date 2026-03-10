package com.dezxxx.hometasks.crud.repository.impl;

import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.WriterRepository;
import com.dezxxx.hometasks.crud.util.JsonUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class GsonWriterRepositoryImpl implements WriterRepository {

    private static final Type LIST_TYPE = new TypeToken<List<Writer>>() {}.getType();
    private final Path storagePath;

    public GsonWriterRepositoryImpl(Path storagePath) {
        this.storagePath = storagePath;
    }

    @Override
    public Writer save(Writer entity) {
        List<Writer> writers = read();

        entity.setId(generateId(writers));

        writers.add(entity);
        write(writers);
        return entity;
    }

    @Override
    public Writer update(Writer entity) {
        validateId(entity.getId());

        List<Writer> writers = read();
        int index = findIndex(writers, entity.getId());

        if (index == -1) {
            throw new NoSuchElementException("Writer not found: id=" + entity.getId());
        }

        if (entity.getStatus() == null) {
            entity.setStatus(writers.get(index).getStatus());
        }

        writers.set(index, entity);
        write(writers);
        return entity;
    }

    @Override
    public Optional<Writer> findById(Long id) {
        return read().stream()
                .filter(writer -> Objects.equals(writer.getId(), id))
                .filter(writer -> writer.getStatus() == Status.ACTIVE)
                .findFirst();
    }

    @Override
    public List<Writer> findAll() {
        return read().stream()
                .filter(writer -> writer.getStatus() == Status.ACTIVE)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        List<Writer> writers = read();

        boolean changed = writers.stream()
                .filter(writer -> Objects.equals(writer.getId(), id))
                .findFirst()
                .map(writer -> {
                    writer.setStatus(Status.DELETED);
                    return true;
                })
                .orElse(false);

        if (!changed) {
            throw new NoSuchElementException("Writer not found: id=" + id);
        }

        write(writers);
    }

    private List<Writer> read() {
        return JsonUtil.readList(storagePath, LIST_TYPE);
    }

    private void write(List<Writer> writers) {
        JsonUtil.writeList(storagePath, writers, LIST_TYPE);
    }

    private Long generateId(List<Writer> writers) {
        return writers.stream()
                .map(Writer::getId)
                .filter(Objects::nonNull)
                .max(Long::compareTo)
                .orElse(0L) + 1;
    }

    private void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
    }

    private int findIndex(List<Writer> writers, Long id) {
        for (int i = 0; i < writers.size(); i++) {
            if (Objects.equals(writers.get(i).getId(), id)) {
                return i;
            }
        }
        return -1;
    }
}
