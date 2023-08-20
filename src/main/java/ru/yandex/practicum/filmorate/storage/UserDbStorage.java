package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Primary
@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        Integer id = jdbcTemplate.queryForObject("select user_id from users where login = ?", Integer.class, user.getLogin());
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        Integer id = user.getId();
        jdbcTemplate.update("update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?",
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), id);
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        jdbcTemplate.update("delete from users where user_id = ?", id);
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from users";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql);
        List<User> users = new ArrayList<>();
        while (userRows.next()) {
            users.add(mapUser(userRows));
        }
        return users;
    }

    @Override
    public User getUserByIdOrThrow(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);
        if (userRows.next()) {
            return mapUser(userRows);
        } else {
            throw new ResourceNotFoundException("Пользователь с таким id не найден.");
        }
    }

    private User mapUser(SqlRowSet userRows) {
        User user = new User(
                userRows.getInt("user_id"),
                userRows.getString("email"),
                userRows.getString("login"),
                userRows.getString("name"),
                Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());

        return user;
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        getUserByIdOrThrow(id);
        getUserByIdOrThrow(friendId);
        jdbcTemplate.update("insert into friend_requests(user_id, friend_user_id) values (?, ?)", id, friendId);
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        getUserByIdOrThrow(id);
        getUserByIdOrThrow(friendId);
        jdbcTemplate.update("delete from friend_requests where friend_user_id = ? and user_id = ?", friendId, id);
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        getUserByIdOrThrow(id);
        getUserByIdOrThrow(friendId);
        List<User> friendsList = new ArrayList<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet("select * from users u where u.user_id in " +
                "(select friend_user_id from friend_requests fs " +
                "where fs.user_id = ?) " +
                "and u.user_id in (select friend_user_id from friend_requests fs " +
                "where fs.user_id = ?)", id, friendId);
        while (rows.next()) {
            friendsList.add(new User(rows.getInt("user_id"),
                    rows.getString("email"),
                    rows.getString("login"),
                    rows.getString("name"),
                    Objects.requireNonNull(rows.getDate("birthday")).toLocalDate()));
        }
        return friendsList;
    }

    @Override
    public List<User> getFriends(Integer id) {
        List<User> friendsList = new ArrayList<>();
        SqlRowSet rows = jdbcTemplate
                .queryForRowSet("select * from users " +
                        "where user_id in " +
                        "(select friend_user_id from friend_requests " +
                        "where user_id = ?)", id);
        while (rows.next()) {
            friendsList.add(new User(rows.getInt("user_id"),
                    rows.getString("email"),
                    rows.getString("login"),
                    rows.getString("name"),
                    Objects.requireNonNull(rows.getDate("birthday")).toLocalDate()));
        }
        return friendsList;
    }

}


