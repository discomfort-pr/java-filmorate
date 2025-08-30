package ru.yandex.practicum.filmorate.util.film;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;
import ru.yandex.practicum.filmorate.util.genre.GenreMapper;
import ru.yandex.practicum.filmorate.util.mpa.MPARatingMapper;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FilmMapper {

    MPARatingMapper ratingMapper;
    GenreMapper genreMapper;

    public Film mapToEntity(FilmDto filmData) {
        return Film.builder()
                .id(filmData.getId())
                .name(filmData.getName())
                .description(filmData.getDescription())
                .releaseDate(filmData.getReleaseDate())
                .duration(filmData.getDuration())
                .mpa(ratingMapper.mapToEntity(filmData.getMpa()))
                .genres(genreMapper.mapToEntity(filmData.getGenres()))
                .build();
    }
}
