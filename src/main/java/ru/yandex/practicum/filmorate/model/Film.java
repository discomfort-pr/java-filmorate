package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.NotBefore;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    public static final LocalDate BIRTHDAY_OF_CINEMA = LocalDate.of(1895, 12, 28);

    @NotNull(groups = UpdateValidationGroup.class)
    @Positive(groups = UpdateValidationGroup.class)
    Long id;

    @NotBlank(groups = CreateValidationGroup.class)
    String name;

    @NotBlank(groups = CreateValidationGroup.class)
    @Size(max = 200, groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    String description;

    @NotBefore(value = "28.12.1895", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    LocalDate releaseDate;

    @NotNull(groups = CreateValidationGroup.class)
    @Positive(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    Integer duration;
}
