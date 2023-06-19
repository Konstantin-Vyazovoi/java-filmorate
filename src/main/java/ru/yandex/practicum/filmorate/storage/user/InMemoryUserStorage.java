package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Primary
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        User newUser = User.builder()
                .id(generateID())
                .login(user.getLogin())
                .email(user.getEmail())
                .name(user.getName())
                .birthday(user.getBirthday())
                .build();
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            throw new NotFoundException("Такого пользователя несуществует!");
        }
        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (user != null) {
            users.remove(user.getId());
        }
    }

    @Override
    public User getUserByID(Integer id) {
        if (users.containsKey(id)) {
            User user = users.get(id);
            return user;
        } else throw new NotFoundException("Такого пользователя несуществует!");
    }

    private int generateID() {
        id++;
        return id;
    }
}
