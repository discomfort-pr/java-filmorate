package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.storage.mpa.MPARowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    String getRatingsByIdQuery = "SELECT * FROM mpa WHERE id = ?";

    JdbcTemplate jdbc;
    MPARowMapper rowMapper;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(jdbc.queryForObject(getRatingsByIdQuery, rowMapper, rs.getInt("mpa_rating_id")))
                .build();
    }
}
