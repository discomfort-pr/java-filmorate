package ru.yandex.practicum.filmorate.model.film.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.film.fields.Genre;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;

import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class Film {

    Integer id;

    String name;

    String description;

    LocalDate releaseDate;

    Integer duration;

    MPARating mpa;

    Set<Genre> genres;

    Set<Integer> likes;
}