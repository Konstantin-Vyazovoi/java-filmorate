package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.excption.NotFoundException;
import ru.yandex.practicum.filmorate.excption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(int userID, int friendID) throws ValidationException {
        HashSet<Integer> userFriends = getFriendsSet(userID);
        HashSet<Integer> friendFriends = getFriendsSet(friendID);
        if (!userFriends.contains(friendID)) {
            userFriends.add(friendID);
            friendFriends.add(userID);
        }
        return userStorage.getUserByID(friendID);
    }

    public User deleteFriend(int userID, int friendID) throws ValidationException {

        HashSet<Integer> userFriends = getFriendsSet(userID);
        HashSet<Integer> friendFriends = getFriendsSet(friendID);
        if (!userFriends.contains(friendID)) {
            userFriends.remove(friendID);
            friendFriends.remove(userID);
        }
        return userStorage.getUserByID(friendID);
    }

    public ArrayList<User> getAllFriends(int userID) throws ValidationException {
        HashSet<Integer> userFriends = getFriendsSet(userID);
        ArrayList<User> friendsList = new ArrayList<>();
        for (Integer friendID : userFriends) {
            User friend = userStorage.getUserByID(friendID);
            friendsList.add(friend);
        }
        return friendsList;
    }

    public ArrayList<User> getCommonFriends(int userID, int otherUserID) throws ValidationException {
        HashSet<Integer> userFriends = getFriendsSet(userID);
        HashSet<Integer> otherUserFriends = getFriendsSet(otherUserID);
        Set<Integer> set = findCommonElements(userFriends, otherUserFriends);
        ArrayList<User> friendsList = new ArrayList<>();
        for (Integer id : set) {
            User friend = userStorage.getUserByID(id);
            friendsList.add(friend);
        }
        return friendsList;
    }

    private HashSet<Integer> getFriendsSet(int id) throws ValidationException {
        User user = userStorage.getUserByID(id);
        validation(user);
        return user.getFriends();
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

    private static Set<Integer> findCommonElements(Set<Integer> first, Set<Integer> second) {
        return first.stream().filter(second::contains).collect(Collectors.toSet());
    }
}
