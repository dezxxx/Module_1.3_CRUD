package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.WriterRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class WriterController {

    private final WriterRepository repository;

    public WriterController(WriterRepository repository) {
        this.repository = repository;
    }

    public Writer create(String firstName, String lastName) {
        validateName(firstName, "firstName");
        validateName(lastName, "lastName");

        Writer writer = new Writer();
        writer.setFirstName(firstName.trim());
        writer.setLastName(lastName.trim());
        writer.setStatus(Status.ACTIVE);

        return repository.save(writer);
    }

    public List<Writer> getAll() {
        return repository.findAll();
    }

    public Writer getById(Long id) {
        validateId(id);
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Writer not found: id=" + id));
    }

    public Writer update(Long id, String firstName, String lastName) {
        validateId(id);
        validateName(firstName, "firstName");
        validateName(lastName, "lastName");

        Writer existing = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Writer not found: id=" + id));

        existing.setFirstName(firstName.trim());
        existing.setLastName(lastName.trim());

        return repository.update(existing);
    }

    public void delete(Long id) {
        validateId(id);
        repository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Id must be positive");
    }

    private void validateName(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}