package ru.yandex.practicum.filmorate.util.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.entity.UserDto;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserFieldScanner {

    public Map<String, Object> extractNonNullFields(UserDto userData) {
        Map<String, Object> nonNullFields = new HashMap<>();

        Field[] fields = userData.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(userData);

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
