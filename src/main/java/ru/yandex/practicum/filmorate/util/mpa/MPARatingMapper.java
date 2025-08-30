package ru.yandex.practicum.filmorate.util.mpa;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;
import ru.yandex.practicum.filmorate.model.film.fields.MPARatingDto;
import ru.yandex.practicum.filmorate.storage.mpa.MPADbStorage;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MPARatingMapper {

    MPADbStorage mpaDbStorage;

    public MPARating mapToEntity(MPARatingDto ratingData) {
        return mpaDbStorage.findOne(ratingData.getId());
    }
}
