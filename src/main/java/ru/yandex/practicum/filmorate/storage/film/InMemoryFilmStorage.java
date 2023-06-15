package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.excption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

@Component
@Primary
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();

    private ArrayList<Film> popularFilms = new ArrayList<>();

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
        Film newFilm = film.withId(generateID());
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

    @Override
    public ArrayList<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        if (count > films.size()) count = films.size();
        ArrayList<Film> mostPopular = new ArrayList<>();
        Comparator<Film> comparator = new Comparator<Film>() {
            @Override
            public int compare(Film film1, Film film2) {
                if (film1.getLikes() == 0 && film2.getLikes() == 0) {
                    return film2.getId() - film1.getId();
                }
                return film2.getLikes() - film1.getLikes();
            }
        };
        popularFilms = getFilms();
        popularFilms.sort(comparator);
        for (int i = 0; i < count; i++) {
            mostPopular.add(popularFilms.get(i));
        }
        return mostPopular;
    }
}
