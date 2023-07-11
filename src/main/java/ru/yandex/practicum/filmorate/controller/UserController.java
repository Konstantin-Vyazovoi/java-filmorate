package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.constraints.Positive;
import java.util.ArrayList;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
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
    public User add(@RequestBody User user) {
        return userService.add(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") @Positive Integer id) {
        return userService.getUserById(id);
    }


    @PutMapping(value = "/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") @Positive Integer id,
                          @PathVariable("friendId") @Positive Integer friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") @Positive Integer id,
                             @PathVariable("friendId") @Positive Integer friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public ArrayList<User> getFriendsList(@PathVariable("id") @Positive Integer id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ArrayList<User> getCommonFriendsList(@PathVariable("id") @Positive Integer id,
                                                @PathVariable("otherId") @Positive Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
