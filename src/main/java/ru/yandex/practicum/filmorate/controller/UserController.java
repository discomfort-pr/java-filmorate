package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long maxId = users.keySet()
                .stream()
                .max(Long::compareTo)
                .orElse(0L);

        return ++maxId;
    }

    private User findSameIdUser(User newInstance) {
        if (!users.containsKey(newInstance.getId())) {
            throw new NotFoundException("Обновляемый пользователь не найден");
        }
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

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Validated(CreateValidationGroup.class) @RequestBody User created) {
        created.setId(getNextId());
        fixUsername(created);

        log.debug("Создан пользователь с id {}, email {}, login {}, name {}, birthday {}",
                created.getId(), created.getEmail(), created.getLogin(), created.getName(), created.getBirthday());

        users.put(created.getId(), created);
        return created;
    }

    @PutMapping
    public User update(@Validated(UpdateValidationGroup.class) @RequestBody User newInstance) {
        User updated = findSameIdUser(newInstance);

        log.debug("Обновление пользователя с id {}, email {}, login {}, name {}, birthday {}",
                updated.getId(), updated.getEmail(), updated.getLogin(), updated.getName(), updated.getBirthday());

        updateUser(updated, newInstance);

        log.debug("Обновлены данные пользователя с id {} на email {}, login {}, name {}, birthday {}",
                updated.getId(), updated.getEmail(), updated.getLogin(),
                updated.getName(), updated.getBirthday());

        users.put(updated.getId(), updated);
        return updated;
    }
}
