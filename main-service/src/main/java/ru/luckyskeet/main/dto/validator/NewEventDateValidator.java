package ru.luckyskeet.main.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.luckyskeet.main.util.constants_and_enums.Constants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NewEventDateValidator implements ConstraintValidator<EventDate, String> {

    @Override
    public boolean isValid(String eventDateTimeString, ConstraintValidatorContext context) {
        if (eventDateTimeString == null) {
            return true;
        } else {
            try {
                LocalDateTime eventDateTime = LocalDateTime.parse(eventDateTimeString,
                        DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT));
                LocalDateTime nowPlusTwoHours = LocalDateTime.now().plusHours(2);
                return !eventDateTime.isBefore(nowPlusTwoHours);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
