package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;

import java.util.List;

public interface FilmStorage {

    List<Film> findAll();

    Film findOne(Integer filmId);

    Film create(FilmDto filmData);

    Film update(FilmDto filmData);

    Film addLike(Integer filmId, Integer userId);

    Film removeLike(Integer filmId, Integer userId);
}
