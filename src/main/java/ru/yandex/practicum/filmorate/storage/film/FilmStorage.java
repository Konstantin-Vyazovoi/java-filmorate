package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;

public interface FilmStorage {
    ArrayList<Film> getFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Film film);

    Film getFilmByID(int id);

    ArrayList<Film> getPopularFilms(Integer count);

    void addLike(int filmID, int userID);

    void deleteLike(int filmID, int userID);

    ArrayList<Genre> getGenres();

    Genre getGenresById(Integer id);

    ArrayList<Mpa> getMpaList();

    Mpa getMpaById(Integer id);
}
