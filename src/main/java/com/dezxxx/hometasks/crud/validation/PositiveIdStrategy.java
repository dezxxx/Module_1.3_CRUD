package com.dezxxx.hometasks.crud.validation;

public class PositiveIdStrategy implements ValidationStrategy<Long> {

    @Override
    public void validate(Long value, String fieldName) {

        if (value == null || value <= 0) {

            throw new IllegalArgumentException(
                    fieldName + " must be positive"
            );
        }
    }
}
