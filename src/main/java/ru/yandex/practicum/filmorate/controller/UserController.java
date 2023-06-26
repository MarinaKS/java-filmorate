package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        List<User> users = userService.getUsers();
        log.info("getUsers list size {}", users.size());
        return users;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("createUser: user = {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("updateUser: user = {}", user);
        if (userService.getUserById(user.getId()) == null) {
            throw new ResourceNotFoundException("нет пользователя с таким id");
        }
        return userService.updateUser(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Integer id) {
        log.info("getUser: id = {}", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addUserFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("addUserFriend: id = {}, friendId = {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("deleteFriend: id = {}, friendId = {}", id, friendId);
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsByUserId(@PathVariable Integer id) {
        log.info("getFriendsByUserId: id = {}", id);
        return userService.getFriendsByUserId(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriendsByUserIds(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("getCommonFriendsByUserIds: id = {}, otherId = {}", id, otherId);
        return userService.getCommonFriendsByUserIds(id, otherId);
    }
}
