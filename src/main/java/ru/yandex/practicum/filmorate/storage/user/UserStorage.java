package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.user.entity.User;
import ru.yandex.practicum.filmorate.model.user.entity.UserDto;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> findAll();

    User findOne(Integer userId);

    User create(UserDto userData);

    User update(UserDto userData);

    User addFriend(Integer userId, Integer friendId);

    User removeFriend(Integer userId, Integer friendId);

    Set<UserDto> getCommonFriends(Integer userId, Integer friendId);
}
