package com.dezxxx.hometasks.crud.controller;

import com.dezxxx.hometasks.crud.model.Writer;
import com.dezxxx.hometasks.crud.service.WriterService;

import java.util.List;

public class WriterController {

    private final WriterService writerService;

    public WriterController(WriterService writerService) {
        this.writerService = writerService;
    }

    public Writer create(String firstName,
                         String lastName) {

        return writerService.create(
                firstName,
                lastName
        );
    }

    public List<Writer> getAll() {

        return writerService.getAll();
    }

    public Writer getById(Long id) {

        return writerService.getById(id);
    }

    public Writer update(Long id,
                         String firstName,
                         String lastName) {

        return writerService.update(
                id,
                firstName,
                lastName
        );
    }

    public void delete(Long id) {

        writerService.delete(id);
    }
}