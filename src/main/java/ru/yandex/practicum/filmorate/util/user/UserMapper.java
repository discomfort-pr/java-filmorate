package ru.yandex.practicum.filmorate.util.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.user.entity.User;
import ru.yandex.practicum.filmorate.model.user.entity.UserDto;

import java.util.HashSet;

@Component
public class UserMapper {

    public User mapToEntity(UserDto userData) {
        return User.builder()
                .id(userData.getId())
                .email(userData.getEmail())
                .login(userData.getLogin())
                .name(userData.getName())
                .birthday(userData.getBirthday())
                .friends(new HashSet<>())
                .build();
    }
}
