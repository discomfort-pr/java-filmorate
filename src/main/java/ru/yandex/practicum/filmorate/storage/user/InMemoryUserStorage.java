package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findUser(Long userId) {
        checkExistence(userId, "Пользователь " + userId + " не найден");
        return users.get(userId);
    }

    @Override
    public User create(User created) {
        created.setId(getNextId());
        fixUsername(created);

        log.info("Создан пользователь с id {}, email {}, login {}, name {}, birthday {}",
                created.getId(), created.getEmail(), created.getLogin(), created.getName(), created.getBirthday());

        users.put(created.getId(), created);
        return created;
    }

    @Override
    public User update(User newInstance) {
        User updated = findSameIdUser(newInstance);

        log.info("Обновление пользователя с id {}, email {}, login {}, name {}, birthday {}",
                updated.getId(), updated.getEmail(), updated.getLogin(), updated.getName(), updated.getBirthday());

        updateUser(updated, newInstance);

        log.info("Обновлены данные пользователя с id {} на email {}, login {}, name {}, birthday {}",
                updated.getId(), updated.getEmail(), updated.getLogin(),
                updated.getName(), updated.getBirthday());

        return updated;
    }

    @Override
    public void delete(Long userId) {
        log.info("Удаление пользователя с id {}", userId);

        users.remove(userId);
    }

    private long getNextId() {
        long maxId = users.keySet()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);

        return ++maxId;
    }

    private void checkExistence(Long userId, String message) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(message);
        }
    }

    private User findSameIdUser(User newInstance) {
        checkExistence(newInstance.getId(), "Обновляемый пользователь не найден");
        return users.get(newInstance.getId());
    }

    private void fixUsername(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void updateUser(User updated, User newInstance) {
        updated.setEmail(Objects.requireNonNullElse(newInstance.getEmail(), updated.getEmail()));
        updated.setLogin(Objects.requireNonNullElse(newInstance.getLogin(), updated.getLogin()));
        updated.setName(newInstance.getName() == null || newInstance.getName().isBlank() ? newInstance.getLogin()
                : newInstance.getName());
        updated.setBirthday(Objects.requireNonNullElse(newInstance.getBirthday(), updated.getBirthday()));
    }
}
