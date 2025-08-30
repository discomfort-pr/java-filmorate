package ru.yandex.practicum.filmorate.model.film.fields;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@AllArgsConstructor
public class Genre {

    Integer id;

    String name;
}
