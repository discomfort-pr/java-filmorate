package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;
import ru.yandex.practicum.filmorate.service.MPAService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class MPAController {

    MPAService mpaService;

    @GetMapping
    public List<MPARating> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{ratingId}")
    public MPARating findOne(@PathVariable Integer ratingId) {
        return mpaService.findOne(ratingId);
    }
}
