package ru.yandex.practicum.filmorate.util.genre;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.fields.Genre;
import ru.yandex.practicum.filmorate.model.film.fields.GenreDto;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GenreMapper {

    GenreDbStorage genreDbStorage;

    public Genre mapToEntity(GenreDto genreData) {
        return genreDbStorage.findOne(genreData.getId());
    }

    public Set<Genre> mapToEntity(Set<GenreDto> genreData) {
        return genreData.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toSet());
    }
}
