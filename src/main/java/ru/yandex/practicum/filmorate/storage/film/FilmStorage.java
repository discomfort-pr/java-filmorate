package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> findAll();

    Film findFilm(Long filmId);

    Film create(Film created);

    Film update(Film newInstance);

    void delete(Long filmId);
}
