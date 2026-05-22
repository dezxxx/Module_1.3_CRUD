package com.dezxxx.hometasks.crud.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    T save(T entity);

    T update(T entity);

    void deleteById(ID id);

    List<T> findAll();

    Optional<T> findById(ID id);
}