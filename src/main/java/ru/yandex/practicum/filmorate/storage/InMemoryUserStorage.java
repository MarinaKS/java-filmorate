package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User createUser(User user) {

        user.setId(++id);
        users.put(user.getId(), user);
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<User>(users.values());
    }

    @Override
    public User getUserByIdOrThrow(Integer id) {
        if (users.get(id) == null) {
            throw new ResourceNotFoundException("нет пользователя с таким id");
        }
        return users.get(id);
    }

    @Override
    public void addFriend(Integer id, Integer friendId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFriend(Integer id, Integer friendId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> getCommonFriends(Integer id, Integer friendId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<User> getFriends(Integer id) {
        throw new UnsupportedOperationException();
    }
}
