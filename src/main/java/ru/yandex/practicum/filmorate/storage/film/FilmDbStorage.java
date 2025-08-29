package ru.yandex.practicum.filmorate.storage.film;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.film.entity.Film;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;
import ru.yandex.practicum.filmorate.model.film.fields.Genre;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;
import ru.yandex.practicum.filmorate.storage.genre.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.film.FilmFieldScanner;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Primary
public class FilmDbStorage implements FilmStorage {

    List<String> updatableFields = List.of("name", "description", "releaseDate", "duration", "mpa");

    Map<String, String> classFieldToTableFieldMappings = Map.of(
            "id", "id",
            "name", "name",
            "description", "description",
            "releaseDate", "release_date",
            "duration", "duration",
            "mpa", "mpa_rating_id"
    );

    JdbcTemplate jdbc;
    FilmRowMapper rowMapper;
    FilmFieldScanner fieldScanner;
    UserStorage userStorage;

    @Override
    public List<Film> findAll() {
        List<Integer> filmsId = getFilmsId();
        return filmsId.stream()
                .map(this::findOne)
                .toList();
    }

    @Override
    public Film findOne(Integer filmId) {
        Film film = getFilm(filmId);
        film.setGenres(getGenres(filmId));
        film.setLikes(getLikes(filmId));
        return film;
    }

    @Override
    public Film create(FilmDto filmData) {
        Integer filmId = insertFilm(filmData);
        if (filmData.getGenres() != null) {
            insertGenres(filmData.getGenres(), filmId);
        }

        log.info("Создан фильм с id {}, name {}, description {}, releaseDate {}, duration {}, mpa {}, genres {}",
                filmId, filmData.getName(), filmData.getDescription(),
                filmData.getReleaseDate(), filmData.getDuration(), filmData.getMpa(), filmData.getGenres());

        return findOne(filmId);
    }

    @Override
    public Film update(FilmDto filmData) {
        Map<String, Object> updatedFields = fieldScanner.extractNonNullFields(filmData);

        Integer filmId = filmData.getId();
        updateFilm(updatedFields, filmId);
        dropGenres(filmId);
        if (updatedFields.containsKey("genres")) {
            insertGenres((Set<Genre>) updatedFields.get("genres"), filmId);
        }

        log.info("Обновление фильма {}", filmId);

        return findOne(filmId);
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        findOne(filmId);
        userStorage.findOne(userId); // проверка на существование

        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbc.update(sql, filmId, userId);

        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);

        return findOne(filmId);
    }

    @Override
    public Film removeLike(Integer filmId, Integer userId) {
        findOne(filmId);
        userStorage.findOne(userId); // проверка на существование

        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);

        log.info("Пользователь {} удаляет лайк с фильма {}", userId, filmId);

        return findOne(filmId);
    }

    private List<Integer> getFilmsId() {
        String sql = "SELECT id FROM films ORDER BY id";
        return jdbc.queryForList(sql, Integer.class);
    }

    private Film getFilm(Integer filmId) {
        String sql = "SELECT * FROM films WHERE id = ?";
        try {
            return jdbc.queryForObject(sql, rowMapper, filmId);
        } catch (EmptyResultDataAccessException exception) {
            throw new FilmNotFoundException("Фильм " + filmId + " не найден");
        }
    }

    private Set<Genre> getGenres(Integer filmId) {
        String sql = "SELECT genre_id FROM film_genres WHERE film_id = ?";
        List<Integer> genresId = jdbc.queryForList(sql, Integer.class, filmId);

        Set<Genre> sortedGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        GenreRowMapper genreRowMapper = new GenreRowMapper();
        for (Integer genreId : genresId) {
            sql = "SELECT * FROM genres WHERE id = ?";
            sortedGenres.add(jdbc.queryForObject(sql, genreRowMapper, genreId));
        }

        return sortedGenres;
    }

    private Set<Integer> getLikes(Integer filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.queryForList(sql, Integer.class, filmId));
    }

    private Integer insertFilm(FilmDto filmData) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, filmData.getName());
            stmt.setString(2, filmData.getDescription());
            stmt.setDate(3, Date.valueOf(filmData.getReleaseDate()));
            stmt.setInt(4, filmData.getDuration());
            stmt.setInt(5, filmData.getMpa().getId());
            return stmt;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    private void insertGenres(Set<Genre> genres, Integer filmId) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbc.update(sql, filmId, genre.getId());
        }
    }

    private void updateFilm(Map<String, Object> updatedFields, Integer filmId) {
        for (String fieldName : updatableFields) {
            if (updatedFields.containsKey(fieldName)) {
                String sql = "UPDATE films SET ";
                sql += classFieldToTableFieldMappings.get(fieldName) + " = ? WHERE id = ?";

                Object value;
                if (fieldName.equals("mpa")) {
                    MPARating rating = (MPARating) updatedFields.get(fieldName);
                    value = rating.getId();
                } else {
                    value = updatedFields.get(fieldName);
                }

                jdbc.update(sql, value, filmId);
            }
        }
    }

    private void dropGenres(Integer filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbc.update(sql, filmId);
    }
}
