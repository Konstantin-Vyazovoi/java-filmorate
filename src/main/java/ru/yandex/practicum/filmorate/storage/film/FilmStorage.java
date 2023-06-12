package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmStorage {
    ArrayList<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Film film);

    Film getFilmByID(int id);

    int generateID();
}
