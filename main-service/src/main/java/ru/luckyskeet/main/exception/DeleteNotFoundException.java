package ru.luckyskeet.main.exception;

import jakarta.validation.ValidationException;

public class DeleteNotFoundException extends ValidationException {

    public DeleteNotFoundException(String message) {
        super(message);
    }
}
