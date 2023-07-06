package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
public class UserService {
    private final UserStorage userStorage;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int userID, int friendID) throws ValidationException {
        return userStorage.addFriend(userID, friendID);
    }

    public User deleteFriend(int userID, int friendID) throws ValidationException {
        return userStorage.deleteFriend(userID, friendID);
    }

    public ArrayList<User> getAllFriends(int userID) throws ValidationException {
        return userStorage.getAllFriends(userID);
    }

    public ArrayList<User> getCommonFriends(int userID, int otherUserID) throws ValidationException {
        return userStorage.getCommonFriends(userID, otherUserID);
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    public User getUserById(int id) {
        return userStorage.getUserByID(id);
    }

    public User update(User user) throws ValidationException {

        validation(user);
        User updateUser = userStorage.updateUser(user);
        if (updateUser == null) {
            log.debug("Некорректный id пользователя");
            throw new NotFoundException("Такого пользователя нет");
        }
        log.trace("Пользователь обновлён");
        return user;
    }

    public User add(User user) throws ValidationException {
        validation(user);
        User newUser = userStorage.addUser(user);
        log.trace("Добавлен новый пользователь");
        return newUser;
    }

    private void validation(User user) throws ValidationException {
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


}
