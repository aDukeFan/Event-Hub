package ru.luckyskeet.main.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class NewUserRequest {
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    String email;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}
