package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

public interface UserStorage {
    ArrayList<User> getUsers();
    User addUser(User user);
    User updateUser(User user);
    void deleteUser(User user);
    User getUserByID(Integer id);
}
