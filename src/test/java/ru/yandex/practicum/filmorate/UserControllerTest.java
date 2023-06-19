package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.excption.NotFoundException;
import ru.yandex.practicum.filmorate.excption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserControllerTest {
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final UserController userController = new UserController(new UserService(userStorage));


    @Test
    void addNewUser() throws ValidationException {
        assertTrue(userStorage.getUsers().isEmpty());
        User user = User.builder()
                .login("login")
                .email("email@mail.ru")
                .name("somename")
                .birthday(LocalDate.of(2000, 12, 20))
                .build();
        userController.add(user);
        ArrayList<User> userList = userStorage.getUsers();
        assertTrue(userList.contains(userStorage.getUserByID(1)));
    }

    @Test
    void addNewUserWithoutName() throws ValidationException {
        User user = User.builder()
                .login("login")
                .email("email@mail.ru")
                .birthday(LocalDate.of(2000, 12, 20))
                .build();
        userController.add(user);
        assertEquals(user.getLogin(), user.getName());
        user.setName(" ");
        userController.add(user);
        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    void addNewUserWithIncorrectLogin() {
        User user = User.builder()
                .email("email@mail.ru")
                .birthday(LocalDate.of(2000, 12, 20))
                .name("somename")
                .build();
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
        user.setLogin(" login incorrect");
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void addNewUserWithIncorrectEmail() {
        User user = User.builder()
                .login("login")
                .birthday(LocalDate.of(2000, 12, 20))
                .name("somename")
                .build();
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
        user.setEmail("Email");
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void addNewUserWithIncorrectBirthday() {
        User user = User.builder()
                .login("login")
                .email("email@mail.ru")
                .name("somename")
                .build();
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
        user.setBirthday(LocalDate.of(2077, 12, 12));
        Assertions.assertThrows(ValidationException.class, () -> userController.add(user));
    }

    @Test
    void updateUser() throws ValidationException {
        User user = User.builder()
                .id(1)
                .login("login")
                .email("email@mail.ru")
                .name("somename")
                .birthday(LocalDate.of(2000, 12, 20))
                .build();
        userController.add(user);
        userController.update(user);
    }

    @Test
    void updateUserWithIncorrectID() throws NotFoundException {
        User user = User.builder()
                .id(0)
                .login("login")
                .email("email@mail.ru")
                .name("somename")
                .birthday(LocalDate.of(2000, 12, 20))
                .build();
        userController.add(user);
        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user));
    }
}
