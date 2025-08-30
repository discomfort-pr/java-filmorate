package ru.yandex.practicum.filmorate.model.user.entity;

//import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import lombok.experimental.FieldDefaults;
//import ru.yandex.practicum.filmorate.serializer.UserSerializer;

import java.time.LocalDate;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
//@JsonSerialize(using = UserSerializer.class)
@Builder
public class User {

    Integer id;

    String email;

    String login;

    String name;

    LocalDate birthday;

    Set<UserDto> friends;
}
