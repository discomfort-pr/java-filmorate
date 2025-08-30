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
import ru.yandex.practicum.filmorate.model.film.fields.GenreDto;
import ru.yandex.practicum.filmorate.model.film.fields.MPARatingDto;
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

    String insertFilmLikeQuery = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    String deleteFilmLikeQuery = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    String getFilmsIdQuery = "SELECT id FROM films ORDER BY id";
    String getFilmByIdQuery = "SELECT * FROM films WHERE id = ?";
    String getGenresIdByFilmIdQuery = "SELECT genre_id FROM film_genres WHERE film_id = ?";
    String getGenreByIdQuery = "SELECT * FROM genres WHERE id = ?";
    String getLikesByFilmIdQuery = "SELECT user_id FROM film_likes WHERE film_id = ?";
    String insertFilmQuery = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) " +
                             "VALUES (?, ?, ?, ?, ?)";
    String insertFilmGenresQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    String updateFilmQueryTemplate = "UPDATE films SET %s = ? WHERE id = ?";
    String deleteFilmGenresQuery = "DELETE FROM film_genres WHERE film_id = ?";
    String getMostLikedFilmsQuery = "SELECT f.*, (SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.id) " +
                                    "AS likes_count FROM films f ORDER BY likes_count DESC LIMIT ?";

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
            insertGenres((Set<GenreDto>) updatedFields.get("genres"), filmId);
        }

        log.info("Обновление фильма {}", filmId);

        return findOne(filmId);
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        findOne(filmId);
        userStorage.findOne(userId); // проверка на существование

        jdbc.update(insertFilmLikeQuery, filmId, userId);

        log.info("Пользователь {} ставит лайк фильму {}", userId, filmId);

        return findOne(filmId);
    }

    @Override
    public Film removeLike(Integer filmId, Integer userId) {
        findOne(filmId);
        userStorage.findOne(userId); // проверка на существование

        jdbc.update(deleteFilmLikeQuery, filmId, userId);

        log.info("Пользователь {} удаляет лайк с фильма {}", userId, filmId);

        return findOne(filmId);
    }

    @Override
    public List<Film> getMostLiked(Integer count) {
        return jdbc.query(getMostLikedFilmsQuery, rowMapper, count);
    }

    private List<Integer> getFilmsId() {
        return jdbc.queryForList(getFilmsIdQuery, Integer.class);
    }

    private Film getFilm(Integer filmId) {
        try {
            return jdbc.queryForObject(getFilmByIdQuery, rowMapper, filmId);
        } catch (EmptyResultDataAccessException exception) {
            throw new FilmNotFoundException("Фильм " + filmId + " не найден");
        }
    }

    private Set<Genre> getGenres(Integer filmId) {
        List<Integer> genresId = jdbc.queryForList(getGenresIdByFilmIdQuery, Integer.class, filmId);

        Set<Genre> sortedGenres = new TreeSet<>(Comparator.comparingInt(Genre::getId));
        GenreRowMapper genreRowMapper = new GenreRowMapper();
        for (Integer genreId : genresId) {
            sortedGenres.add(jdbc.queryForObject(getGenreByIdQuery, genreRowMapper, genreId));
        }

        return sortedGenres;
    }

    private Set<Integer> getLikes(Integer filmId) {
        return new HashSet<>(jdbc.queryForList(getLikesByFilmIdQuery, Integer.class, filmId));
    }

    private Integer insertFilm(FilmDto filmData) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(insertFilmQuery, new String[]{"id"});
            stmt.setString(1, filmData.getName());
            stmt.setString(2, filmData.getDescription());
            stmt.setDate(3, Date.valueOf(filmData.getReleaseDate()));
            stmt.setInt(4, filmData.getDuration());
            stmt.setInt(5, filmData.getMpa().getId());
            return stmt;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    private void insertGenres(Set<GenreDto> genres, Integer filmId) {
        for (GenreDto genre : genres) {
            jdbc.update(insertFilmGenresQuery, filmId, genre.getId());
        }
    }

    private void updateFilm(Map<String, Object> updatedFields, Integer filmId) {
        for (String fieldName : updatableFields) {
            if (updatedFields.containsKey(fieldName)) {
                Object value;
                if (fieldName.equals("mpa")) {
                    MPARatingDto rating = (MPARatingDto) updatedFields.get(fieldName);
                    value = rating.getId();
                } else {
                    value = updatedFields.get(fieldName);
                }

                jdbc.update(
                        String.format(updateFilmQueryTemplate, classFieldToTableFieldMappings.get(fieldName)),
                        value, filmId
                );
            }
        }
    }

    private void dropGenres(Integer filmId) {
        jdbc.update(deleteFilmGenresQuery, filmId);
    }
}
