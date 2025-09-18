package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MPARowMapper implements RowMapper<MPARating> {

    @Override
    public MPARating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MPARating(rs.getInt("id"), rs.getString("name"));
    }
}
