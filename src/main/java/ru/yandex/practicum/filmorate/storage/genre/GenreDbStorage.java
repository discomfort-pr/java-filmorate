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

    JdbcTemplate jdbc;
    GenreRowMapper rowMapper;

    public List<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbc.query(sql, rowMapper);
    }

    public Genre findOne(Integer genreId) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, rowMapper, genreId);
        } catch (EmptyResultDataAccessException exception) {
            throw new GenreNotFoundException("Жанр " + genreId + " не найден");
        }
    }
}
