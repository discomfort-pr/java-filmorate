package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidatorTest {

    private FilmValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new FilmValidator();
    }

    @Test
    public void shouldNotAcceptEmptyReleaseDateForCreating() {
        Film film1 = new Film(null, "name", "desc", null, 30);

        assertThrows(ValidationException.class, () -> validator.validateForCreating(film1));
    }

    @Test
    public void shouldNotAcceptInvalidReleaseDate() {
        Film film1 = new Film(null, "name", "desc", LocalDate.of(1895, 12, 27), 30);

        assertThrows(ValidationException.class, () -> validator.validateForCreating(film1));

    }
}
