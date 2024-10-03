package ru.luckyskeet.main.request;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class UpdateCompilationRequest {
    List<Integer> events;
    Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
