package ru.yandex.practicum.filmorate.model.film.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.annotation.NotBefore;
import ru.yandex.practicum.filmorate.annotation.TablePresent;
import ru.yandex.practicum.filmorate.model.film.fields.GenreDto;
import ru.yandex.practicum.filmorate.model.film.fields.MPARatingDto;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {

    @NotNull(groups = UpdateValidationGroup.class)
    @Positive(groups = UpdateValidationGroup.class)
    Integer id;

    @NotBlank(groups = CreateValidationGroup.class)
    String name;

    @NotBlank(groups = CreateValidationGroup.class)
    @Size(max = 200, groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    String description;

    @NotNull(groups = CreateValidationGroup.class)
    @NotBefore(value = "28.12.1895", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    LocalDate releaseDate;

    @NotNull(groups = CreateValidationGroup.class)
    @Positive(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    Integer duration;

    @TablePresent(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    MPARatingDto mpa;

    @TablePresent(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    Set<GenreDto> genres;
}
