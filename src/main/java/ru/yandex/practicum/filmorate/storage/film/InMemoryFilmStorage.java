package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryFilmStorage implements FilmStorage{
    private final HashMap<Integer, Film> films = new HashMap<>();

    private int id = 0;

    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteFilm(Film film) {
        if (film != null){
            films.remove(film.getId());
        }
    }
    @Override
    public Film addFilm(Film film){
        Film newFilm = Film.builder()
                .id(generateID())
                .name(film.getName())
                .description(film.getDescription())
                .duration(film.getDuration())
                .releaseDate(film.getReleaseDate())
                .build();
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film){

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            return null;
        }
        return film;
    }

    public HashMap<Integer, Film> getFilmMap() {
        return films;
    }

    private int generateID() {
        id++;
        return id;
    }
}
