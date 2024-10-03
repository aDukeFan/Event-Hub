package ru.luckyskeet.main.exception;

import jakarta.validation.ValidationException;

public class BadTimFrameException extends ValidationException {

    public BadTimFrameException(String message) {
        super(message);
    }
}
