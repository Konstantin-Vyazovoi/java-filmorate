package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

public interface UserStorage {
    public ArrayList<User> getUsers();
    public User addUser(User user);
    public User updateUser(User user);
    public void deleteUser(User user);
    public User getUserByID(int id);
}
