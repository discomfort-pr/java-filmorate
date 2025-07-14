package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
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

    public User(User oldInstance, User newInstance) {
        id = oldInstance.id;
        email = (newInstance.email == null) ? oldInstance.email : newInstance.email;
        login = (newInstance.login == null) ? oldInstance.login : newInstance.login;
        name = (newInstance.name == null || newInstance.name.isBlank()) ? newInstance.login : newInstance.name;
        birthday = (newInstance.birthday == null) ? oldInstance.birthday : newInstance.birthday;
    }
}
