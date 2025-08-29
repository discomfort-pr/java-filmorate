package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.annotation.TablePresent;
import ru.yandex.practicum.filmorate.model.film.fields.Genre;
import ru.yandex.practicum.filmorate.model.film.fields.MPARating;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PresentValidator implements ConstraintValidator<TablePresent, Object> {

    JdbcTemplate jdbc;

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Set) {
            Set<Genre> genres = (Set<Genre>) obj;

            List<Integer> tableGenres = jdbc.queryForList("SELECT id FROM genres", Integer.class);
            Set<Integer> genresId = genres.stream()
                    .map(Genre::getId)
                    .filter(id -> !tableGenres.contains(id))
                    .collect(Collectors.toSet());

            return genresId.isEmpty();
        } else {
            MPARating rating = (MPARating) obj;

            List<Integer> allRatings = jdbc.queryForList("SELECT id FROM mpa", Integer.class);
            return allRatings.contains(rating.getId());
        }
    }
}
