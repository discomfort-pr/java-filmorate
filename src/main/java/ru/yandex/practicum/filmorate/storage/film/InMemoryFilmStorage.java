package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.film.FilmMapper;

import java.util.*;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    Map<Integer, Film> films = new HashMap<>();

    FilmMapper mapper;
    UserStorage userStorage;

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findOne(Integer filmId) {
        checkExistence(filmId, "Фильм " + filmId + " не найден");
        return films.get(filmId);
    }

    @Override
    public Film create(FilmDto filmData) {
        filmData.setId(getNextId());

        Film entity = mapper.mapToEntity(filmData);
        films.put(filmData.getId(), entity);

        log.info("Создан фильм с id {}, name {}, description {}, releaseDate {}, duration {}, mpa {}",
                filmData.getId(), filmData.getName(), filmData.getDescription(),
                filmData.getReleaseDate(), filmData.getDuration(), filmData.getMpa());

        return entity;
    }

    @Override
    public Film update(FilmDto filmData) {
        Film updated = findOne(filmData.getId());

        log.info("Обновление фильма с id {}, name {}, description {}, releaseDate {}, duration {}, mpa {}, genres {}",
                updated.getId(), updated.getName(), updated.getDescription(),
                updated.getReleaseDate(), updated.getDuration(), updated.getMpa(), updated.getGenres());

        updateFilmData(updated, filmData);

        log.info("Обновлены данные фильма с id {} на name {}, description {}, releaseDate {}, duration {}, mpa {}, genres {}",
                updated.getId(), updated.getName(), updated.getDescription(),
                updated.getReleaseDate(), updated.getDuration(), updated.getMpa(), updated.getGenres());

        return updated;
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        Film liked = findOne(filmId);
        userStorage.findOne(userId);

        liked.getLikes().add(userId);

        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);

        return liked;
    }

    @Override
    public Film removeLike(Integer filmId, Integer userId) {
        Film liked = findOne(filmId);
        userStorage.findOne(userId);

        liked.getLikes().remove(userId);

        log.info("Пользователь {} удаляет лайк с фильма {}", userId, filmId);

        return liked;
    }

    @Override
    public List<Film> getMostLiked(Integer count) {
        return findAll()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .toList();
    }

    private Integer getNextId() {
        Integer maxId = films.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);

        return ++maxId;
    }

    private void checkExistence(Integer filmId, String message) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(message);
        }
    }

    private void updateFilmData(Film updated, FilmDto filmData) {
        Film converted = mapper.mapToEntity(filmData);

        updated.setName(Objects.requireNonNullElse(converted.getName(), updated.getName()));
        updated.setDescription(Objects.requireNonNullElse(converted.getDescription(), updated.getDescription()));
        updated.setReleaseDate(Objects.requireNonNullElse(converted.getReleaseDate(), updated.getReleaseDate()));
        updated.setDuration(Objects.requireNonNullElse(converted.getDuration(), updated.getDuration()));
        updated.setMpa(Objects.requireNonNullElse(converted.getMpa(), updated.getMpa()));
        updated.setGenres(Objects.requireNonNullElse(converted.getGenres(), updated.getGenres()));
    }
}
