package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;
    private final UserController userController;
    User user;
    User friend;
    User commonFriend;

    @BeforeEach
    void newUser() {
        user = User.builder()
                .id(1)
                .email("email@mail")
                .login("login")
                .birthday(LocalDate.of(2000, 12, 17))
                .build();
    }

    @AfterEach
    void afterEach() {
        userStorage.deleteUsers();
    }

    @Test
    void addNewUser() throws ValidationException {
        assertTrue(userStorage.getUsers().isEmpty());
        userController.add(user);
        List<User> userList = userStorage.getUsers();
        assertTrue(userList.contains(userStorage.getUserByID(user.getId())));
    }

    @Test
    void addNewUserWithoutName() throws ValidationException {
        user.setEmail("newmail@aa");
        user.setLogin("l1");
        userController.add(user);
        assertEquals(user.getLogin(), user.getName());
        user.setName(" ");
        user.setEmail("newmail@bb");
        user.setLogin("l2");
        userController.add(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void addNewUserWithIncorrectLogin() {
        user.setLogin(null);
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
        user.setLogin(" login incorrect");
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void addNewUserWithIncorrectEmail() {
        user.setEmail(null);
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
        user.setEmail("Email");
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void addNewUserWithIncorrectBirthday() {
        user.setBirthday(LocalDate.of(2077, 12, 12));
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void updateUser() throws ValidationException {
        User addUser = userController.add(user);
        User updateUser = userController.update(user);
        assertEquals(addUser, updateUser);
    }

    @Test
    void updateUserWithIncorrectID() throws NotFoundException {
        user.setId(0);
        userController.add(user);
        user.setId(-1);
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user));
    }

    @Test
    void deleteUser() {
        userController.add(user);
        user = userStorage.getUserByID(user.getId());
        userStorage.deleteUser(user);
        assertTrue(userController.getUsers().isEmpty());
    }

    @BeforeEach
    void newFriends() {
        friend = User.builder()
                .login("loginFriend")
                .email("email@gmail.ru")
                .name("somename")
                .birthday(LocalDate.of(2000, 12, 20))
                .build();
        commonFriend = User.builder()
                .login("commonFriend")
                .email("email@gmail.com")
                .name("Frank")
                .birthday(LocalDate.of(2000, 12, 20))
                .build();
    }

    @Test
    void addFriendToUser() {
        user.setEmail("somemail@mail");
        userController.add(user);
        userController.add(friend);
        userController.addFriend(user.getId(), friend.getId());
        List<User> friendList = userController.getFriendsList(user.getId());
        assertTrue(friendList.contains(friend));
    }

    @Test
    void getCommonFriend() {
        userController.add(user);
        userController.add(friend);
        userController.add(commonFriend);
        userController.addFriend(user.getId(), commonFriend.getId());
        userController.addFriend(friend.getId(), commonFriend.getId());
        ArrayList<User> friendList = userController.getCommonFriendsList(user.getId(), friend.getId());
        assertTrue(friendList.contains(commonFriend));
    }

}