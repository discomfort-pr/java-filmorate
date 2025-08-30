package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.annotation.TablePresent;
import ru.yandex.practicum.filmorate.model.film.fields.GenreDto;
import ru.yandex.practicum.filmorate.model.film.fields.MPARatingDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PresentValidator implements ConstraintValidator<TablePresent, Object> {

    String getGenresIdQuery = "SELECT id FROM genres";
    String getRatingsIdQuery = "SELECT id FROM mpa";

    JdbcTemplate jdbc;

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Set) {
            Set<GenreDto> genres = (Set<GenreDto>) obj;

            List<Integer> tableGenres = jdbc.queryForList(getGenresIdQuery, Integer.class);
            Set<Integer> genresId = genres.stream()
                    .map(GenreDto::getId)
                    .filter(id -> !tableGenres.contains(id))
                    .collect(Collectors.toSet());

            return genresId.isEmpty();
        } else {
            MPARatingDto rating = (MPARatingDto) obj;

            List<Integer> allRatings = jdbc.queryForList(getRatingsIdQuery, Integer.class);
            return allRatings.contains(rating.getId());
        }
    }
}
