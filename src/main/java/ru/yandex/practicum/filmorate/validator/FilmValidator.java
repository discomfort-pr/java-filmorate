package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static ru.yandex.practicum.filmorate.model.Film.BIRTHDAY_OF_CINEMA;

public class FilmValidator {

    public void validateForCreating(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMA)) {
            throw new ValidationException("Дата релиза не может быть пустой или раньше 28.12.1895");
        }
    }

    public void validateForUpdating(Film film) {
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(BIRTHDAY_OF_CINEMA)) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }
}
