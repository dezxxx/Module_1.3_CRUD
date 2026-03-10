package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.repository.LabelRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class LabelController {

    private final LabelRepository labelRepository;

    public LabelController(LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    public Label create(String name) {
        validateName(name);

        Label label = new Label();
        label.setName(name.trim());
        label.setStatus(Status.ACTIVE);

        return labelRepository.save(label);
    }

    public List<Label> getAll() {
        return labelRepository.findAll();
    }

    public Label getById(Long id) {
        validateId(id);

        return labelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Label not found: id=" + id));
    }

    public Label update(Long id, String name) {
        validateId(id);
        validateName(name);

        Label existing = getById(id);
        existing.setName(name.trim());

        return labelRepository.update(existing);
    }

    public void delete(Long id) {
        validateId(id);
        labelRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be positive");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be blank");
        }
    }
}
