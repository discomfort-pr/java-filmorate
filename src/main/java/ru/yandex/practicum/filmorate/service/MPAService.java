package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class MPAService {

    MPADbStorage storage;

    public List<MPARating> findAll() {
        return storage.findAll();
    }

    public MPARating findOne(Integer ratingId) {
        return storage.findOne(ratingId);
    }
}
