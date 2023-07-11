package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(User user);

    User getUserByID(Integer id);

    ArrayList<User> getCommonFriends(int userID, int otherUserID);

    ArrayList<User> getAllFriends(int userID);

    void addFriend(int userID, int friendID) throws ValidationException;

    void deleteFriend(int userID, int friendID);
}
