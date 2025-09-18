package ru.yandex.practicum.filmorate.storage.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.user.entity.User;
import ru.yandex.practicum.filmorate.model.user.entity.UserDto;
import ru.yandex.practicum.filmorate.util.user.UserFieldScanner;
import ru.yandex.practicum.filmorate.util.user.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Primary
public class UserDbStorage implements UserStorage {

    List<String> updatableFields = List.of("email", "login", "name", "birthday");

    String getUsersIdQuery = "SELECT id FROM users ORDER BY id";
    String getUserByIdQuery = "SELECT * FROM users WHERE id = ?";
    String getUserFriendsQuery = "SELECT friend_id FROM friendships WHERE user_id = ?";
    String insertUserQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    String updateUserQueryTemplate = "UPDATE users SET %s = ? WHERE id = ?";
    String insertFriendshipRequestQuery = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    String deleteFriendshipRequestQuery = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
    String getCommonFriendsQuery =
            """
            SELECT u.*
            FROM users u
            WHERE u.id IN (
                SELECT friend_id
                FROM friendships
                WHERE user_id = ? AND friend_id != ?
            )
            AND u.id IN (
                SELECT friend_id
                FROM friendships
                WHERE user_id = ? AND friend_id != ?
            );
            """;

    JdbcTemplate jdbc;
    UserMapper mapper;
    UserRowMapper rowMapper;
    UserFieldScanner fieldScanner;

    @Override
    public List<User> findAll() {
        List<Integer> usersId = getUsersId();
        return usersId.stream()
                .map(this::findOne)
                .toList();
    }

    @Override
    public User findOne(Integer userId) {
        User user = getUser(userId);
        user.setFriends(getFriends(userId));
        return user;
    }

    @Override
    public User create(UserDto userData) {
        fillUsername(userData);
        Integer userId = insertUser(userData);

        log.info("Создан пользователь с id {}, email {}, login {}, name {}, birthday {}",
                userData.getId(), userData.getEmail(), userData.getLogin(),
                userData.getName(), userData.getBirthday());

        return findOne(userId);
    }

    @Override
    public User update(UserDto userData) {
        Map<String, Object> updatedFields = fieldScanner.extractNonNullFields(userData);

        Integer userId = userData.getId();
        updateUser(updatedFields, userId);

        log.info("Обновление пользователя {}", userId);

        return findOne(userId);
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        insertFriendshipRequest(userId, friendId);

        log.info("Пользователь {} добавил в друзья {}", userId, friendId);

        return findOne(userId);
    }

    @Override
    public User removeFriend(Integer userId, Integer friendId) {
        removeFriendshipRequest(userId, friendId);

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);

        return findOne(userId);
    }

    @Override
    public Set<UserDto> getCommonFriends(Integer userId, Integer friendId) {
        List<User> queryResult = jdbc.query(getCommonFriendsQuery, rowMapper, userId, friendId, friendId, userId);

        return queryResult.stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toSet());
    }

    private List<Integer> getUsersId() {
        return jdbc.queryForList(getUsersIdQuery, Integer.class);
    }

    private User getUser(Integer userId) {
        try {
            return jdbc.queryForObject(getUserByIdQuery, rowMapper, userId);
        } catch (EmptyResultDataAccessException exception) {
            throw new UserNotFoundException("Пользователь " + userId + " не найден");
        }
    }

    private Set<UserDto> getFriends(Integer userId) {
        return new HashSet<>(jdbc.queryForList(getUserFriendsQuery, Integer.class, userId)).stream()
                .map(id -> mapper.mapToDto(findOne(id)))
                .collect(Collectors.toSet());
    }

    private void fillUsername(UserDto userData) {
        if (userData.getName() == null || userData.getName().isBlank()) {
            userData.setName(userData.getLogin());
        }
    }

    private Integer insertUser(UserDto userData) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(insertUserQuery, new String[]{"id"});
            stmt.setString(1, userData.getEmail());
            stmt.setString(2, userData.getLogin());
            stmt.setString(3, userData.getName());
            stmt.setDate(4, Date.valueOf(userData.getBirthday()));
            return stmt;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    private void updateUser(Map<String, Object> updatedFields, Integer userId) {
        for (String fieldName : updatableFields) {
            if (updatedFields.containsKey(fieldName)) {
                jdbc.update(String.format(updateUserQueryTemplate, fieldName), updatedFields.get(fieldName), userId);
            }
        }
    }

    private void checkExistence(Integer userId, Integer friendId) {
        findOne(userId);
        findOne(friendId);
    }

    private void insertFriendshipRequest(Integer userId, Integer friendId) {
        checkExistence(userId, friendId);
        jdbc.update(insertFriendshipRequestQuery, userId, friendId);
    }

    private void removeFriendshipRequest(Integer userId, Integer friendId) {
        checkExistence(userId, friendId);
        jdbc.update(deleteFriendshipRequestQuery, userId, friendId);
    }
}
