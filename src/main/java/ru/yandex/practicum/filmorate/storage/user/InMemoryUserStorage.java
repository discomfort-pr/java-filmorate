package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.entity.User;
import ru.yandex.practicum.filmorate.model.user.entity.UserDto;
import ru.yandex.practicum.filmorate.util.user.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    Map<Integer, User> users = new HashMap<>();

    UserMapper mapper;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findOne(Integer userId) {
        checkExistence(userId, "Пользователь " + userId + " не найден");
        return users.get(userId);
    }

    @Override
    public User create(UserDto userData) {
        userData.setId(getNextId());
        fillUsername(userData);

        User entity = mapper.mapToEntity(userData);

        log.info("Создан пользователь с id {}, email {}, login {}, name {}, birthday {}",
                userData.getId(), userData.getEmail(), userData.getLogin(),
                userData.getName(), userData.getBirthday());

        users.put(userData.getId(), entity);
        return entity;
    }

    @Override
    public User update(UserDto userData) {
        User updated = findOne(userData.getId());

        log.info("Обновление пользователя с id {}, email {}, login {}, name {}, birthday {}",
                updated.getId(), updated.getEmail(), updated.getLogin(),
                updated.getName(), updated.getBirthday());

        updateUserData(updated, userData);

        log.info("Обновлены данные пользователя с id {} на email {}, login {}, name {}, birthday {}",
                updated.getId(), updated.getEmail(), updated.getLogin(),
                updated.getName(), updated.getBirthday());

        return updated;
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        User user = findOne(userId);
        findOne(friendId);

        user.getFriends().add(mapper.mapToDto(findOne(friendId)));

        log.info("Пользователь {} добавил в друзья {}", userId, friendId);

        return user;
    }

    @Override
    public User removeFriend(Integer userId, Integer friendId) {
        User user = findOne(userId);
        findOne(friendId);

        user.getFriends().remove(mapper.mapToDto(findOne(friendId)));

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);

        return user;
    }

    @Override
    public Set<UserDto> getCommonFriends(Integer userId, Integer friendId) {
        Set<UserDto> friendSet1 = findOne(userId).getFriends();
        Set<UserDto> friendSet2 = findOne(friendId).getFriends();

        Set<UserDto> generalSet = new HashSet<>();
        generalSet.addAll(friendSet1);
        generalSet.addAll(friendSet2);

        return generalSet.stream()
                .filter(user -> friendSet1.contains(user) && friendSet2.contains(user))
                .collect(Collectors.toSet());
    }

    private Integer getNextId() {
        Integer maxId = users.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(0);

        return ++maxId;
    }

    private void checkExistence(Integer userId, String message) {
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException(message);
        }
    }

    private void fillUsername(UserDto userData) {
        if (userData.getName() == null || userData.getName().isBlank()) {
            userData.setName(userData.getLogin());
        }
    }

    private void updateUserData(User updated, UserDto userData) {
        updated.setEmail(Objects.requireNonNullElse(userData.getEmail(), updated.getEmail()));
        updated.setLogin(Objects.requireNonNullElse(userData.getLogin(), updated.getLogin()));
        updated.setName(userData.getName() == null || userData.getName().isBlank()
                ? userData.getLogin()
                : userData.getName());
        updated.setBirthday(Objects.requireNonNullElse(userData.getBirthday(), updated.getBirthday()));
    }
}
