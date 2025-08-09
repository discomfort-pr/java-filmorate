package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FilmController {

    FilmStorage filmStorage;
    FilmService filmService;

    UserStorage userStorage;

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @PostMapping
    public Film create(@Validated(CreateValidationGroup.class) @RequestBody Film created) {
        return filmStorage.create(created);
    }

    @PutMapping
    public Film update(@Validated(UpdateValidationGroup.class) @RequestBody Film newInstance) {
        return filmStorage.update(newInstance);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") Long filmId, @PathVariable Long userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostLiked(@RequestParam(defaultValue = "10") String count) {
        return filmService.getMostLiked(Integer.parseInt(count));
    }
}
