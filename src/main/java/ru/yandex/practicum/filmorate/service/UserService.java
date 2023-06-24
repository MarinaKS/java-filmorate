package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }


    public User updateUser(User user) {
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
        userStorage.getUserByIdOrThrow(userId).getFriends().add(friendId);
        userStorage.getUserByIdOrThrow(friendId).getFriends().add(userId);
        return true;
    }

    public boolean deleteFriend(Integer userId, Integer friendId) {
        userStorage.getUserByIdOrThrow(userId).getFriends().remove(friendId);
        userStorage.getUserByIdOrThrow(friendId).getFriends().remove(userId);
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
        ArrayList<User> friendsList = new ArrayList<>();
        for (Integer id : userStorage.getUserByIdOrThrow(userId).getFriends()) {
            if (userStorage.getUserByIdOrThrow(otherId).getFriends().contains(id)) {
                friendsList.add(userStorage.getUserByIdOrThrow(id));
            }
        }
        return friendsList;
    }
}
