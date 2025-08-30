package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;

import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class MPADbStorage {

    String getAllRatingsQuery = "SELECT * FROM mpa";
    String getRatingByIdQuery = "SELECT * FROM mpa WHERE id = ?";

    JdbcTemplate jdbc;
    MPARowMapper rowMapper;

    public List<MPARating> findAll() {
        return jdbc.query(getAllRatingsQuery, rowMapper);
    }

    public MPARating findOne(Integer ratingId) {
        try {
            return jdbc.queryForObject(getRatingByIdQuery, rowMapper, ratingId);
        } catch (EmptyResultDataAccessException exception) {
            throw new RatingNotFoundException("Рейтинг " + ratingId + " не найден");
        }
    }
}
