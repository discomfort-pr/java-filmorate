package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private static final String DEFAULT_ERROR_LOG_MESSAGE = "Валидация данных фильма провалена";
    private final Map<Long, Film> films = new HashMap<>();
    private final FilmValidator validator = new FilmValidator();

    private long getNextId() {
        long maxId = films.keySet()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);

        return ++maxId;
    }

    private Film findSameIdFilm(Film newInstance) {
        if (!films.containsKey(newInstance.getId())) {
            throw new NotFoundException("Обновляемый фильм не найден");
        }
        return films.get(newInstance.getId());
    }

    private Film updateFilm(Film oldInstance, Film newInstance) {
        return new Film(oldInstance, newInstance);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Validated(CreateValidationGroup.class) @RequestBody Film created) {
        try {
            validator.validateForCreating(created);
        } catch (ValidationException exception) {
            log.debug(DEFAULT_ERROR_LOG_MESSAGE, exception);
            throw exception;
        }
        created.setId(getNextId());
        log.debug("Создан фильм с id {}, name {}, description {}, releaseDate {}, duration {}",
                created.getId(), created.getName(), created.getDescription(),
                created.getReleaseDate(), created.getDuration());

        films.put(created.getId(), created);
        return created;
    }

    @PutMapping
    public Film update(@Validated(UpdateValidationGroup.class) @RequestBody Film newInstance) {
        Film oldInstance = findSameIdFilm(newInstance);
        try {
            validator.validateForUpdating(newInstance);
        } catch (ValidationException exception) {
            log.error(DEFAULT_ERROR_LOG_MESSAGE, exception);
            throw exception;
        }

        log.debug("Обновление фильма с id {}, name {}, description {}, releaseDate {}, duration {}",
                oldInstance.getId(), oldInstance.getName(), oldInstance.getDescription(),
                oldInstance.getReleaseDate(), oldInstance.getDuration());
        Film updated = updateFilm(oldInstance, newInstance);
        log.debug("Обновлены данные фильма с id {} на name {}, description {}, releaseDate {}, duration {}",
                updated.getId(), updated.getName(), updated.getDescription(),
                updated.getReleaseDate(), updated.getDuration());
        films.put(newInstance.getId(), updated);
        return updated;
    }
}
