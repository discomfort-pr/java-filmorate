package ru.yandex.practicum.filmorate.util.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.entity.FilmDto;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class FilmFieldScanner {

    public Map<String, Object> extractNonNullFields(FilmDto filmData) {
        Map<String, Object> nonNullFields = new HashMap<>();

        Field[] fields = filmData.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(filmData);

                if (value != null) {
                    nonNullFields.put(name, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return nonNullFields;
    }
}
