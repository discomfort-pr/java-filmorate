package ru.yandex.practicum.filmorate.model.film.fields;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@AllArgsConstructor
@Validated
public class Genre {

    @NonNull
    @NotNull
    @Positive
    Integer id;

    String name;
}
