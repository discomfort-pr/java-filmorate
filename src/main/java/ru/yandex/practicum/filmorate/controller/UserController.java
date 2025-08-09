package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validationgroup.CreateValidationGroup;
import ru.yandex.practicum.filmorate.validationgroup.UpdateValidationGroup;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class UserController {

    UserStorage userStorage;
    UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @PostMapping
    public User create(@Validated(CreateValidationGroup.class) @RequestBody User created) {
        return userStorage.create(created);
    }

    @PutMapping
    public User update(@Validated(UpdateValidationGroup.class) @RequestBody User newInstance) {
        return userStorage.update(newInstance);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Long applicant, @PathVariable("friendId") Long friend) {
        return userService.addFriend(applicant, friend);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User removeFriend(@PathVariable("id") Long applicant, @PathVariable("friendId") Long friend) {
        return userService.removeFriend(applicant, friend);
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriends(@PathVariable("id") Long userId) {
        return userStorage.findUser(userId).getFriends();
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Set<User> getMutualFriends(@PathVariable("id") Long applicant, @PathVariable("otherId") Long other) {
        return userService.getMutualFriends(applicant, other);
    }
}
