package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Data
public class Film {

    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Mpa mpa;
    private List<Genre> genres;
    private HashSet<Integer> likesIdSet = new HashSet<>();
    private int likes = likesIdSet.size();
    private int id;

    public Film() {
    }

    @Builder
    public Film(String name,
                String description,
                Mpa mpa,
                List<Genre> genres,
                LocalDate releaseDate,
                long duration,
                HashSet<Integer> likesIdSet,
                int likes,
                int id) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.id = id;
        this.genres = genres;
    }

    public String getGenreId() {
        if (genres != null) {
            return genres.toString();
        }
        return "null";
    }
}

