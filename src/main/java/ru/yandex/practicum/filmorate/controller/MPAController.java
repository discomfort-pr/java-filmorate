package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class MPAController {

    MPADbStorage mpaDbStorage;

    @GetMapping
    public List<MPARating> findAll() {
        return mpaDbStorage.findAll();
    }

    @GetMapping("/{ratingId}")
    public MPARating findOne(@PathVariable Integer ratingId) {
        return mpaDbStorage.findOne(ratingId);
    }
}
