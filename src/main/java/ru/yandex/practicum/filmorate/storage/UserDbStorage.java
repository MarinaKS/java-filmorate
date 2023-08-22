package ru.yandex.practicum.filmorate.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"USER_ID"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, (Date.valueOf(user.getBirthday())));
            return ps;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
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
        return jdbcTemplate.query(sql, this::mapUser);
    }

    @Override
    public User getUserByIdOrThrow(Integer id) {
        try {
            return jdbcTemplate.queryForObject("select * from users where user_id = ?", this::mapUser, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Пользователь с таким id не найден.");
        }
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

    private User mapUser(ResultSet rows, int rowNum) throws SQLException {
        return new User(
                rows.getInt("user_id"),
                rows.getString("email"),
                rows.getString("login"),
                rows.getString("name"),
                Objects.requireNonNull(rows.getDate("birthday")).toLocalDate());
    }
}


