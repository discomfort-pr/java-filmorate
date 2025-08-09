package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class FilmService {

    FilmStorage filmStorage;

    UserStorage userStorage;

    public Film addLike(Long filmId, Long userId) {
        userStorage.findUser(userId);

        Film film = filmStorage.findFilm(filmId);
        film.getLikes().add(userId);

        log.debug("Пользователь {} ставит лайк фильму {}", userId, filmId);

        return film;
    }

    public Film removeLike(Long filmId, Long userId) {
        userStorage.findUser(userId);

        Film film = filmStorage.findFilm(filmId);
        film.getLikes().remove(userId);

        log.debug("Пользователь {} убирает лайк с фильма {}", userId, filmId);

        return film;
    }

    public List<Film> getMostLiked(int count) {
        return filmStorage.findAll()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .toList();
    }
}
