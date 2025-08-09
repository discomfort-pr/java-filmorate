package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    User findUser(Long userId);

    User create(User created);

    User update(User newInstance);

    void delete(Long userId);
}
