package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Hashtable;

@Data
public class User {

    private final int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Hashtable<Integer, Boolean> friends;

    @Builder
    public User(int id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new Hashtable<>();
    }

}
