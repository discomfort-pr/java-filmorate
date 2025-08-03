package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

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

    private void updateFilm(Film updated, Film newInstance) {
        updated.setName(Objects.requireNonNullElse(newInstance.getName(), updated.getName()));
        updated.setDescription(Objects.requireNonNullElse(newInstance.getDescription(), updated.getDescription()));
        updated.setReleaseDate(Objects.requireNonNullElse(newInstance.getReleaseDate(), updated.getReleaseDate()));
        updated.setDuration(Objects.requireNonNullElse(newInstance.getDuration(), updated.getDuration()));
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Validated(CreateValidationGroup.class) @RequestBody Film created) {
        created.setId(getNextId());

        log.debug("Создан фильм с id {}, name {}, description {}, releaseDate {}, duration {}",
                created.getId(), created.getName(), created.getDescription(),
                created.getReleaseDate(), created.getDuration());

        films.put(created.getId(), created);
        return created;
    }

    @PutMapping
    public Film update(@Validated(UpdateValidationGroup.class) @RequestBody Film newInstance) {
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
}
