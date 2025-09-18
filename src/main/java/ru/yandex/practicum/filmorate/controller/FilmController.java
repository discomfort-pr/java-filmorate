package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmController {

    FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{filmId}")
    public Film findOne(@PathVariable Integer filmId) {
        return filmService.findOne(filmId);
    }

    @PostMapping
    public Film create(@Validated(CreateValidationGroup.class) @RequestBody FilmDto created) {
        return filmService.create(created);
    }

    @PutMapping
    public Film update(@Validated(UpdateValidationGroup.class) @RequestBody FilmDto newInstance) {
        return filmService.update(newInstance);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable("id") Integer filmId, @PathVariable Integer userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getMostLiked(@RequestParam(defaultValue = "10") String count) {
        return filmService.getMostLiked(Integer.parseInt(count));
    }
}
