package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        validateUserName(user);
        return userStorage.createUser(user);
    }


    public User updateUser(User user) {
        validateUserName(user);
        return userStorage.updateUser(user);
    }


    public void deleteUser(Integer id) {
        userStorage.deleteUser(id);
    }


    public List<User> getUsers() {
        return userStorage.getUsers();
    }


    public User getUserById(Integer id) {
        return userStorage.getUserByIdOrThrow(id);
    }

    public boolean addFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserByIdOrThrow(userId);
        User friend = userStorage.getUserByIdOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return true;
    }

    public boolean deleteFriend(Integer userId, Integer friendId) {
        User user = userStorage.getUserByIdOrThrow(userId);
        User friend = userStorage.getUserByIdOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return true;
    }

    public ArrayList<User> getFriendsByUserId(Integer id) {
        ArrayList<User> friendsList = new ArrayList<>();
        for (Integer friendId : userStorage.getUserByIdOrThrow(id).getFriends()) {
            friendsList.add(userStorage.getUserByIdOrThrow(friendId));
        }
        return friendsList;
    }

    public ArrayList<User> getCommonFriendsByUserIds(Integer userId, Integer otherId) {
        Set<Integer> userFriends = new HashSet<>(userStorage.getUserByIdOrThrow(userId).getFriends());
        Set<Integer> otherFriends = new HashSet<>(userStorage.getUserByIdOrThrow(otherId).getFriends());
        userFriends.retainAll(otherFriends);
        ArrayList<User> friendsList = new ArrayList<>();
        for (Integer id : userFriends) {
            friendsList.add(userStorage.getUserByIdOrThrow(id));
        }
        return friendsList;
    }

    private void validateUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
