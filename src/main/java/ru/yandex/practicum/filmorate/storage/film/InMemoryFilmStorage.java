package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    Map<Long, Film> films = new HashMap<>();

    private long getNextId() {
        long maxId = films.keySet()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);

        return ++maxId;
    }

    private void checkExistence(Long filmId, String message) {
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(message);
        }
    }

    private Film findSameIdFilm(Film newInstance) {
        checkExistence(newInstance.getId(), "Обновляемый фильм не найден");
        return films.get(newInstance.getId());
    }

    private void updateFilm(Film updated, Film newInstance) {
        updated.setName(Objects.requireNonNullElse(newInstance.getName(), updated.getName()));
        updated.setDescription(Objects.requireNonNullElse(newInstance.getDescription(), updated.getDescription()));
        updated.setReleaseDate(Objects.requireNonNullElse(newInstance.getReleaseDate(), updated.getReleaseDate()));
        updated.setDuration(Objects.requireNonNullElse(newInstance.getDuration(), updated.getDuration()));
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findFilm(Long filmId) {
        checkExistence(filmId, "Фильм " + filmId + " не найден");
        return films.get(filmId);
    }

    @Override
    public Film create(Film created) {
        created.setId(getNextId());

        log.debug("Создан фильм с id {}, name {}, description {}, releaseDate {}, duration {}",
                created.getId(), created.getName(), created.getDescription(),
                created.getReleaseDate(), created.getDuration());

        films.put(created.getId(), created);
        return created;
    }

    @Override
    public Film update(Film newInstance) {
        Film updated = findSameIdFilm(newInstance);

        log.debug("Обновление фильма с id {}, name {}, description {}, releaseDate {}, duration {}",
                updated.getId(), updated.getName(), updated.getDescription(),
                updated.getReleaseDate(), updated.getDuration());

        updateFilm(updated, newInstance);

        log.debug("Обновлены данные фильма с id {} на name {}, description {}, releaseDate {}, duration {}",
                updated.getId(), updated.getName(), updated.getDescription(),
                updated.getReleaseDate(), updated.getDuration());

        films.put(updated.getId(), updated);
        return updated;
    }

    @Override
    public void delete(Long filmId) {
        log.debug("Удаление фильма с id {}", filmId);

        films.remove(filmId);
    }
}
