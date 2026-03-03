package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.LabelRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class LabelController {

    private final LabelRepository repository;

    public LabelController(LabelRepository repository) {
        this.repository = repository;
    }

    public Label create(String name) {
        validateName(name);

        Label label = new Label();
        label.setName(name.trim());
        label.setStatus(Status.ACTIVE);

        return repository.save(label);
    }

    public List<Label> getAll() {
        return repository.findAll();
    }

    public Label getById(Long id) {
        validateId(id);

        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Label not found: id=" + id));
    }

    public Label update(Long id, String newName) {
        validateId(id);
        validateName(newName);

        Label existing = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Label not found: id=" + id));

        existing.setName(newName.trim());
        return repository.update(existing);
    }

    public void delete(Long id) {
        validateId(id);
        repository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Id must be positive");
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name must not be blank");
    }
}