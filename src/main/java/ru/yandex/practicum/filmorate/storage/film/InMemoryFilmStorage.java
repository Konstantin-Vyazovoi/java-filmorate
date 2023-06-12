package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();

    private int id = 0;

    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void deleteFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
        } else throw new NotFoundException("Такого фильма не существует!");
    }

    @Override
    public Film getFilmByID(int id) {
        if (films.containsKey(id)) {
            Film film = films.get(id);
            return film;
        } else throw new NotFoundException("Такого фильма не существует!");
    }

    @Override
    public Film addFilm(Film film) {
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
    public Film updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else throw new NotFoundException("Такого фильма не существует!");
        return film;
    }

    public HashMap<Integer, Film> getFilmMap() {
        return films;
    }

    @Override
    public int generateID() {
        id++;
        return id;
    }
}
