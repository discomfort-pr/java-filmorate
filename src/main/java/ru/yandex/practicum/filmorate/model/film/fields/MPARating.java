package ru.yandex.practicum.filmorate.model.film.fields;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@AllArgsConstructor
public class MPARating {

    Integer id;

    String name;
}
