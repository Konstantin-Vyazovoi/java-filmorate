package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Genre {

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    private Integer id;
    private String name;

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
