package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.serializer.UserSerializer;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize(using = UserSerializer.class)
public class User {

    @NotNull(groups = UpdateValidationGroup.class)
    @Positive(groups = UpdateValidationGroup.class)
    Long id;

    @NotBlank(groups = CreateValidationGroup.class)
    @Email(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    String email;

    @NotBlank(groups = CreateValidationGroup.class)
    @Pattern(regexp = "^\\S+$", groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    String login;

    String name;

    @NotNull(groups = CreateValidationGroup.class)
    @PastOrPresent(groups = {CreateValidationGroup.class, UpdateValidationGroup.class})
    LocalDate birthday;

    @EqualsAndHashCode.Exclude
    Set<User> friends = new HashSet<>();
}
