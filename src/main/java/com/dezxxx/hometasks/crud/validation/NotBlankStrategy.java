package com.dezxxx.hometasks.crud.validation;

public class NotBlankStrategy implements ValidationStrategy<String> {

    @Override
    public void validate(String value, String fieldName) {

        if (value == null || value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }
    }
}
