package ru.yandex.practicum.filmorate.storage.genre;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.film.fields.Genre;

import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class GenreDbStorage {

    String getAllGenresQuery = "SELECT * FROM genres";
    String getGenreByIdQuery = "SELECT * FROM genres WHERE id = ?";

    JdbcTemplate jdbc;
    GenreRowMapper rowMapper;

    public List<Genre> findAll() {
        return jdbc.query(getAllGenresQuery, rowMapper);
    }

    public Genre findOne(Integer genreId) {
        try {
            return jdbc.queryForObject(getGenreByIdQuery, rowMapper, genreId);
        } catch (EmptyResultDataAccessException exception) {
            throw new GenreNotFoundException("Жанр " + genreId + " не найден");
        }
    }
}
