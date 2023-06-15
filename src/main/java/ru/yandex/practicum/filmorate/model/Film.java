package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.time.LocalDate;
import java.util.HashSet;

@Data
public class Film {

    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    HashSet<Integer> likesIdSet = new HashSet<>();
    private int likes = likesIdSet.size();
    @With
    private final int id;

    @Builder
    public Film(String name, String description, LocalDate releaseDate, long duration, HashSet<Integer> likesIdSet, int likes, int id) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = id;
    }
}

