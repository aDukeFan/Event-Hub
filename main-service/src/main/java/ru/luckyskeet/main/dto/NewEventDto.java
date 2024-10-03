package ru.luckyskeet.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import ru.luckyskeet.main.dto.validator.EventDate;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@ToString
public class NewEventDto {
    @NotBlank
    @NotNull
    @Size(min = 20, max = 2000)
    String annotation;
    Integer category;
    @NotBlank
    @NotNull
    @Size(min = 20, max = 7000)
    String description;
    @EventDate
    @NotNull
    String eventDate;
    LocationDto location;
    boolean paid = false;
    @PositiveOrZero
    Integer participantLimit = 0;
    boolean requestModeration = true;
    @Size(min = 3, max = 120)
    String title;
}
