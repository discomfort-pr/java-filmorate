package ru.yandex.practicum.filmorate.model.user.entity;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserDto {

    @NotNull(groups = UpdateValidationGroup.class)
    @Positive(groups = UpdateValidationGroup.class)
    Integer id;

    @NotBlank(groups = CreateValidationGroup.class)
    @Email(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    String email;

    @NotBlank(groups = CreateValidationGroup.class)
    @Pattern(regexp = "^\\S+$", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    String login;

    String name;

    @NotNull(groups = CreateValidationGroup.class)
    @PastOrPresent(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    LocalDate birthday;
}
