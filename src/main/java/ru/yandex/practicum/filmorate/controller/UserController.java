package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.excption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ArrayList<User> getUsers() {
        return userService.getUsers();
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    @PostMapping
    public User add(@RequestBody User user){
        return userService.add(user);
    }

    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId){
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer id, @PathVariable("friendId") Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/users/{id}/friends")
    public ArrayList<User> getFriendsList(@PathVariable("id") Integer id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriendsList(@PathVariable("id") Integer id,
                                                @PathVariable("otherId") Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
