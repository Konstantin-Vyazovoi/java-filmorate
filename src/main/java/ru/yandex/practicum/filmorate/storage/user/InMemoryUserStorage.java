package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage{

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
            return null;
        }
        return user;
    }

    @Override
    public void deleteUser(User user) {
        if (user != null){
            users.remove(user.getId());
        }
    }

    @Override
    public User getUserByID(int id) {
        User user = null;
        if (id > 0) {
            user = users.get(id);
        }
        return user;
    }

    private int generateID() {
        id++;
        return id;
    }
}
