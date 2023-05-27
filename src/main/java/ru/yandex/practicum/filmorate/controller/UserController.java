package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.excption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/users")
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PutMapping(value = "/users")
    public User update(@RequestBody User user) throws ValidationException {

        validation(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            log.debug("Некорректный id пользователя");
            throw new ValidationException("Такого пользователя нет");
        }
        log.trace("Пользователь обновлён");
        return user;
    }

    @PostMapping(value = "/users")
    public User add(@RequestBody User user) throws ValidationException {
        validation(user);
        User newUser = User.builder()
                .id(generateID())
                .login(user.getLogin())
                .email(user.getEmail())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
        users.put(newUser.getId(), newUser);
        log.trace("Добавлен новый пользователь");
        return newUser;
    }

    public HashMap<Integer, User> getUsersMap() {
        return users;
    }

    private void validation(User user) throws ValidationException {
        if (stringValidation(user.getLogin())) {
            log.debug("Не корректный логин");
            throw new ValidationException("Не корректный логин");
        }
        if (user.getEmail() == null || stringValidation(user.getEmail()) || !user.getEmail().contains("@")) {
            log.debug("Не корректная электронная почта");
            throw new ValidationException("Не корректная электронная почта");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        } else if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Не корректная дата рождения");
            throw new ValidationException("Не корректная дата рождения");
        }
    }

    private boolean stringValidation(String string) {
        return string == null || string.isBlank() || string.contains(" ");
    }

    private int generateID() {
        id++;
        return id;
    }
}
