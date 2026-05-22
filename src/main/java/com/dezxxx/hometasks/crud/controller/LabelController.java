package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Label;
import com.dezxxx.hometasks.crud.service.LabelService;

import java.util.List;

public class LabelController {

    private final LabelService service;

    public LabelController(LabelService service) {
        this.service = service;
    }

    public Label create(String name) {
        return service.create(name);
    }

    public List<Label> getAll() {
        return service.getAll();
    }

    public Label getById(Long id) {
        return service.getById(id);
    }

    public Label update(Long id, String name) {
        return service.update(id, name);
    }

    public void delete(Long id) {
        service.delete(id);
    }
}