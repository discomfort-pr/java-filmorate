package ru.yandex.practicum.filmorate.util.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;

@Component
public class FilmMapper {

    public Film mapToEntity(FilmDto filmData) {
        return Film.builder()
                .id(filmData.getId())
                .name(filmData.getName())
                .description(filmData.getDescription())
                .releaseDate(filmData.getReleaseDate())
                .duration(filmData.getDuration())
                .mpa(filmData.getMpa())
                .genres(filmData.getGenres())
                .build();
    }
}
