package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class UserService {

    private final UserStorage userStorage;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userID, int friendID) {
        idValidation(userID);
        idValidation(friendID);
        userStorage.addFriend(userID, friendID);
    }

    public void deleteFriend(int userID, int friendID) {
        idValidation(userID);
        idValidation(friendID);
        userStorage.deleteFriend(userID, friendID);
    }

    public ArrayList<User> getAllFriends(int userID) {
        idValidation(userID);
        return userStorage.getAllFriends(userID);
    }

    public ArrayList<User> getCommonFriends(int userID, int otherUserID) {
        idValidation(userID);
        idValidation(otherUserID);
        return userStorage.getCommonFriends(userID, otherUserID);
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public User getUserById(int id) {
        idValidation(id);
        return userStorage.getUserByID(id);
    }

    public User update(User user) {

        validation(user);
        User updateUser = userStorage.updateUser(user);
        if (updateUser == null) {
            log.debug("Некорректный id пользователя");
            throw new NotFoundException("Такого пользователя нет");
        }
        log.trace("Пользователь обновлён");
        return user;
    }

    public User add(User user) {
        validation(user);
        User newUser = userStorage.addUser(user);
        log.trace("Добавлен новый пользователь");
        return newUser;
    }

    private void validation(User user) {
        if (user == null) {
            log.debug("Пользователь не существует");
            throw new ValidationException("Пользователь не существует");
        }
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

    private void idValidation(int id) {
        if (id <= 0) {
            log.debug("Не корректный id");
            throw new NotFoundException("Не корректный id");
        }
    }
}
