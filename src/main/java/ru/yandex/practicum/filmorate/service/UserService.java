package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userDbStorage;

    @Autowired
    public UserService(UserStorage userDbStorage) {
        this.userDbStorage = userDbStorage;
    }

    public User createUser(User user) {
        validateUserName(user);
        return userDbStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUserName(user);
        return userDbStorage.updateUser(user);
    }

    public void deleteUser(Integer id) {
        userDbStorage.deleteUser(id);
    }

    public List<User> getUsers() {
        return userDbStorage.getUsers();
    }

    public User getUserById(Integer id) {
        return userDbStorage.getUserByIdOrThrow(id);
    }

    public void addFriend(Integer userId, Integer friendId) {
        userDbStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userDbStorage.deleteFriend(userId, friendId);
    }

    public List<User> getFriendsByUserId(Integer id) {
        return userDbStorage.getFriends(id);
    }

    public List<User> getCommonFriendsByUserIds(Integer userId, Integer otherId) {
        return userDbStorage.getCommonFriends(userId, otherId);
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
