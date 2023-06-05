package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;

@Data
public class Film {
    private final int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    HashSet<Integer> likesIdSet = new HashSet<>();
    private int likesCount = likesIdSet.size();

    @Builder
    public Film(int id, String description, String name, LocalDate releaseDate, long duration) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}

