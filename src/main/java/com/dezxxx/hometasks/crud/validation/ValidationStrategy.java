package com.dezxxx.hometasks.crud.validation;

public interface ValidationStrategy<T> {

    void validate(T value, String fieldName);
}
