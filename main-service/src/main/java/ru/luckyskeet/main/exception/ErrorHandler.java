package ru.luckyskeet.main.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.luckyskeet.main.util.constants_and_enums.Constants;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class ErrorHandler {

    private final String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter
                    .ofPattern(Constants.DATE_TIME_FORMAT));

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        String message = String.format("Field: %s. Error: %s. Value: %s",
                Objects.requireNonNull(exception.getFieldError()).getField(),
                exception.getFieldError().getDefaultMessage(),
                exception.getFieldError().getRejectedValue());
        return new ErrorResponse()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setReason("Incorrectly made request.")
                .setMessage(message)
                .setTimestamp(timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(final SQLException exception) {
        return new ErrorResponse()
                .setStatus(HttpStatus.CONFLICT)
                .setReason("Integrity constraint has been violated.")
                .setMessage(exception.getMessage())
                .setTimestamp(timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDelete(final EmptyResultDataAccessException exception) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(Objects.requireNonNull(exception.getMessage()));
        StringBuilder builder = new StringBuilder();
        builder.append("Object with id=");
        while (matcher.find()) {
            builder.append(matcher.group());
        }
        builder.append(" was not found");
        return new ErrorResponse()
                .setStatus(HttpStatus.NOT_FOUND)
                .setReason("The required object was not found.")
                .setMessage(builder.toString())
                .setTimestamp(timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGetUsers(final MethodArgumentTypeMismatchException exception) {
        return new ErrorResponse()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setReason("Incorrectly made request")
                .setMessage(exception.getMessage())
                .setTimestamp(timestamp);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException entityNotFoundException) {
        return new ErrorResponse()
                .setStatus(HttpStatus.NOT_FOUND)
                .setReason("Bad ID")
                .setMessage(entityNotFoundException.getMessage())
                .setTimestamp(timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleValidationException(final ValidationException exception) {
        return new ErrorResponse()
                .setStatus(HttpStatus.CONFLICT)
                .setReason("Incorrectly made request")
                .setMessage(exception.getMessage())
                .setTimestamp(timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadTimeFrameException(final BadTimFrameException exception) {
        return new ErrorResponse()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setReason("Bad time frame")
                .setMessage(exception.getMessage())
                .setTimestamp(timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleMaxLimit(final MaxLimitException exception) {
        return new ErrorResponse()
                .setStatus(HttpStatus.CONFLICT)
                .setReason("Limit is max")
                .setMessage(exception.getMessage())
                .setTimestamp(timestamp);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDeleteException(final DeleteNotFoundException exception) {
        return new ErrorResponse()
                .setStatus(HttpStatus.NOT_FOUND)
                .setReason("Already delete or not found")
                .setMessage(exception.getMessage())
                .setTimestamp(timestamp);
    }


}
