package ru.yandex.practicum.filmorate.model.film.fields;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Validated
public class MPARatingDto {

    @NotNull
    @Positive
    Integer id;

    @JsonCreator
    public MPARatingDto(@JsonProperty("id") Integer id) {
        this.id = id;
    }
}
