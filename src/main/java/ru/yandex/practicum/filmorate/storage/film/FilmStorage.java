package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.excption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmStorage {
    public ArrayList<Film> getFilms();
    public Film addFilm(Film film);
    public Film updateFilm(Film film);
    public void deleteFilm(Film film);

}
