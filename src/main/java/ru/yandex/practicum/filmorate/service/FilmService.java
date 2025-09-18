package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class FilmService {

    FilmStorage filmStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findOne(Integer filmId) {
        return filmStorage.findOne(filmId);
    }

    public Film create(FilmDto created) {
        return filmStorage.create(created);
    }

    public Film update(FilmDto newInstance) {
        return filmStorage.update(newInstance);
    }

    public Film addLike(Integer filmId, Integer userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film removeLike(Integer filmId, Integer userId) {
        return filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getMostLiked(Integer count) {
        return filmStorage.getMostLiked(count);
    }
}
