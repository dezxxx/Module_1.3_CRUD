package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Post;
import com.dezxxx.hometasks.crud.model.Status;
import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.WriterRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class WriterController {

    private final WriterRepository writerRepository;

    public WriterController(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    public Writer create(String firstName, String lastName, List<Post> posts) {
        validateText(firstName, "firstName");
        validateText(lastName, "lastName");

        Writer writer = new Writer();
        writer.setFirstName(firstName.trim());
        writer.setLastName(lastName.trim());
        writer.setStatus(Status.ACTIVE);
        writer.setPosts(posts);

        return writerRepository.save(writer);
    }

    public List<Writer> getAll() {
        return writerRepository.findAll();
    }

    public Writer getById(Long id) {
        validateId(id);

        return writerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Writer not found: id=" + id));
    }

    public Writer update(Long id, String firstName, String lastName, List<Post> posts) {
        validateId(id);
        validateText(firstName, "firstName");
        validateText(lastName, "lastName");

        Writer existing = getById(id);
        existing.setFirstName(firstName.trim());
        existing.setLastName(lastName.trim());
        existing.setPosts(posts);

        return writerRepository.update(existing);
    }

    public void delete(Long id) {
        validateId(id);
        writerRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Id must be positive");
        }
    }

    private void validateText(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
    }
}
