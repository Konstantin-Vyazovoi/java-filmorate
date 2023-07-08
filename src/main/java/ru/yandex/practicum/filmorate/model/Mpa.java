package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {
    public Mpa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    private int id;
    private String name;

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
