package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.entity.User;
import ru.yandex.practicum.filmorate.model.user.entity.UserDto;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class UserService {

    UserStorage storage;

    public List<User> findAll() {
        return storage.findAll();
    }

    public User create(UserDto userData) {
        return storage.create(userData);
    }

    public User update(UserDto userData) {
        return storage.update(userData);
    }

    public User addFriend(Integer applicant, Integer friend) {
        return storage.addFriend(applicant, friend);
    }

    public User removeFriend(Integer applicant, Integer friend) {
        return storage.removeFriend(applicant, friend);
    }

    public Set<UserDto> getFriends(Integer userId) {
        return storage.findOne(userId).getFriends();
    }

    public Set<UserDto> getMutualFriends(Integer applicant, Integer other) {
        return storage.getCommonFriends(applicant, other);
    }
}
