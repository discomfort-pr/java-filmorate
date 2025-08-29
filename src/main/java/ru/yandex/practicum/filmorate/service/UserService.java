package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.user.entity.User;
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

    public Set<Integer> getMutualFriends(Integer applicant, Integer other) {
        Set<Integer> friendSet1 = storage.findOne(applicant).getFriends();
        Set<Integer> friendSet2 = storage.findOne(other).getFriends();

        Set<Integer> generalSet = new HashSet<>();

        generalSet.addAll(friendSet1);
        generalSet.addAll(friendSet2);

        return generalSet
                .stream()
                .filter(userId -> friendSet1.contains(userId) && friendSet2.contains(userId))
                .collect(Collectors.toSet());
    }
}
