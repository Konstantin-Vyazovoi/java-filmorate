package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

public interface UserStorage {
    public ArrayList<User> getUsers();
    public void addUser();
    public void updateUser();
    public void deleteUser();
}
