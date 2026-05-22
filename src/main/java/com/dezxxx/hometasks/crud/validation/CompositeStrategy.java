package com.dezxxx.hometasks.crud.validation;

import java.util.List;

public class CompositeStrategy<T> implements ValidationStrategy<T> {

    private final List<ValidationStrategy<T>> strategies;

    @SafeVarargs
    public CompositeStrategy(ValidationStrategy<T>... strategies) {
        this.strategies = List.of(strategies);
    }

    @Override
    public void validate(T value, String fieldName) {
        for (ValidationStrategy<T> strategy : strategies) {
            strategy.validate(value, fieldName);
        }
    }
}
