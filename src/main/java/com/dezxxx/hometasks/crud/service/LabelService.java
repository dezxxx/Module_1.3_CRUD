package com.dezxxx.hometasks.crud.service;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.repository.LabelRepository;
import com.dezxxx.hometasks.crud.validation.ValidationStrategy;

import java.util.List;
import java.util.NoSuchElementException;

public class LabelService {

    private final LabelRepository repository;
    private final ValidationStrategy<String> textValidator;
    private final ValidationStrategy<Long> idValidator;

    public LabelService(LabelRepository repository,
                        ValidationStrategy<String> textValidator,
                        ValidationStrategy<Long> idValidator) {
        this.repository = repository;
        this.textValidator = textValidator;
        this.idValidator = idValidator;
    }

    public Label create(String name) {

        textValidator.validate(name, "name");

        Label label = new Label();
        label.setName(name.trim());

        return repository.save(label);
    }

    public List<Label> getAll() {
        return repository.findAll();
    }

    public Label getById(Long id) {

        idValidator.validate(id, "id");

        return repository.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException(
                                "Label not found: " + id
                        )
                );
    }

    public Label update(Long id, String name) {

        idValidator.validate(id, "id");
        textValidator.validate(name, "name");

        Label label = getById(id);
        label.setName(name.trim());

        return repository.update(label);
    }

    public void delete(Long id) {

        idValidator.validate(id, "id");

        repository.deleteById(id);
    }
}
