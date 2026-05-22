package com.dezxxx.hometasks.crud.service;

import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.repository.WriterRepository;
import com.dezxxx.hometasks.crud.validation.ValidationStrategy;

import java.util.List;
import java.util.NoSuchElementException;

public class WriterService {

    private final WriterRepository repository;
    private final ValidationStrategy<String> textValidator;
    private final ValidationStrategy<Long> idValidator;

    public WriterService(WriterRepository repository,
                         ValidationStrategy<String> textValidator,
                         ValidationStrategy<Long> idValidator) {
        this.repository = repository;
        this.textValidator = textValidator;
        this.idValidator = idValidator;
    }

    public Writer create(String firstName, String lastName) {

        textValidator.validate(firstName, "firstName");
        textValidator.validate(lastName, "lastName");

        Writer writer = new Writer();
        writer.setFirstName(firstName.trim());
        writer.setLastName(lastName.trim());

        return repository.save(writer);
    }

    public List<Writer> getAll() {
        return repository.findAll();
    }

    public Writer getById(Long id) {

        idValidator.validate(id, "id");

        return repository.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Writer not found: " + id
                        )
                );
    }

    public Writer update(Long id, String firstName, String lastName) {

        idValidator.validate(id, "id");
        textValidator.validate(firstName, "firstName");
        textValidator.validate(lastName, "lastName");

        Writer writer = getById(id);
        writer.setFirstName(firstName.trim());
        writer.setLastName(lastName.trim());

        return repository.update(writer);
    }

    public void delete(Long id) {

        idValidator.validate(id, "id");

        repository.deleteById(id);
    }
}
