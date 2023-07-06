package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
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

    @Override
    public void addFriend(int userID, int friendID) throws ValidationException {
        HashSet<Integer> userFriends = getFriendsSet(userID);
        HashSet<Integer> friendFriends = getFriendsSet(friendID);
        if (!userFriends.contains(friendID)) {
            userFriends.add(friendID);
            friendFriends.add(userID);
        }
    }

    @Override
    public void deleteFriend(int userID, int friendID) throws ValidationException {

        HashSet<Integer> userFriends = getFriendsSet(userID);
        HashSet<Integer> friendFriends = getFriendsSet(friendID);
        if (!userFriends.contains(friendID)) {
            userFriends.remove(friendID);
            friendFriends.remove(userID);
        }
    }

    @Override
    public ArrayList<User> getAllFriends(int userID) throws ValidationException {
        HashSet<Integer> userFriends = getFriendsSet(userID);
        ArrayList<User> friendsList = new ArrayList<>();
        for (Integer friendID : userFriends) {
            User friend = getUserByID(friendID);
            friendsList.add(friend);
        }
        return friendsList;
    }

    @Override
    public ArrayList<User> getCommonFriends(int userID, int otherUserID) {
        HashSet<Integer> userFriends = getFriendsSet(userID);
        HashSet<Integer> otherUserFriends = getFriendsSet(otherUserID);
        Set<Integer> set = findCommonElements(userFriends, otherUserFriends);
        ArrayList<User> friendsList = new ArrayList<>();
        for (Integer id : set) {
            User friend = getUserByID(id);
            friendsList.add(friend);
        }
        return friendsList;
    }

    private HashSet<Integer> getFriendsSet(int id) throws ValidationException {
        User user = getUserByID(id);
        HashSet<Integer> friends = (HashSet<Integer>) user.getFriends().keySet();
        return friends;
    }

    private static Set<Integer> findCommonElements(Set<Integer> first, Set<Integer> second) {
        return first.stream().filter(second::contains).collect(Collectors.toSet());
    }
}
