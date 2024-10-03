package ru.luckyskeet.main.exception;

import jakarta.validation.ValidationException;

public class MaxLimitException extends ValidationException {
    public MaxLimitException(String message) {
        super(message);
    }
}
