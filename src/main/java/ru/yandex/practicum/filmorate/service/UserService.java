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
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class UserService {

    UserStorage storage;

    public User addFriend(Integer applicant, Integer friend) {
        return storage.addFriend(applicant, friend);
    }

    public User removeFriend(Integer applicant, Integer friend) {
        return storage.removeFriend(applicant, friend);
    }

    public Set<UserDto> getMutualFriends(Integer applicant, Integer other) {
        Set<UserDto> friendSet1 = storage.findOne(applicant).getFriends();
        Set<UserDto> friendSet2 = storage.findOne(other).getFriends();

        Set<UserDto> generalSet = new HashSet<>();

        generalSet.addAll(friendSet1);
        generalSet.addAll(friendSet2);

        return generalSet
                .stream()
                .filter(user -> friendSet1.contains(user) && friendSet2.contains(user))
                .collect(Collectors.toSet());
    }
}
