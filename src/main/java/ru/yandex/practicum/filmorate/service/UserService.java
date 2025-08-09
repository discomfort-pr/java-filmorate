package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserStorage storage;

    public User addFriend(Long applicant, Long friend) {
        User user1 = storage.findUser(applicant);
        User user2 = storage.findUser(friend);

        user1.getFriends().add(user2);
        user2.getFriends().add(user1);

        log.debug("Пользователь {} добавил в друзья {}", applicant, friend);

        return user1;
    }

    public User removeFriend(Long applicant, Long friend) {
        User user1 = storage.findUser(applicant);
        User user2 = storage.findUser(friend);

        user1.getFriends().remove(user2);
        user2.getFriends().remove(user1);

        log.debug("Пользователь {} удалил из друзей {}", applicant, friend);

        return user1;
    }

    public Set<User> getMutualFriends(Long applicant, Long other) {
        Set<User> friendSet1 = storage.findUser(applicant).getFriends();
        Set<User> friendSet2 = storage.findUser(other).getFriends();

        Set<User> generalSet = new HashSet<>();

        generalSet.addAll(friendSet1);
        generalSet.addAll(friendSet2);

        return new HashSet<>(generalSet)
                .stream()
                .filter(user -> friendSet1.contains(user) && friendSet2.contains(user))
                .collect(Collectors.toSet());
    }
}
