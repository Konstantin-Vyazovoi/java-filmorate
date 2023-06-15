package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.excption.NotFoundException;
import ru.yandex.practicum.filmorate.excption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

@Service
@Primary
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    public void addLike(int filmID, int userID) {
        Film film = filmStorage.getFilmByID(filmID);
        Set likes = film.getLikesIdSet();
        likes.add(userID);
    }

    public void deleteLike(int filmID, int userID) {
        Film film = filmStorage.getFilmByID(filmID);
        Set likes = film.getLikesIdSet();
        if (!likes.contains(userID)) throw new NotFoundException("Такой пользователь не ставил лайк!");
        likes.remove(userID);
    }

    public ArrayList<Film> getPopularFilms(Integer count) {
        return filmStorage.getPopularFilms(count);
    }

    public ArrayList<Film> getFilms() {
        return new ArrayList<>(filmStorage.getFilms());
    }

    public Film add(Film film) {
        validation(film);
        Film addFilm = filmStorage.addFilm(film);
        log.trace("Добавлен новый фильм");
        return addFilm;
    }

    public Film update(Film film) {
        validation(film);
        Film updateFilm = filmStorage.updateFilm(film);
        log.trace("Фильм с id {} обновлен", updateFilm.getId());
        return updateFilm;
    }

    private void validation(Film film) throws ValidationException {
        LocalDate oldDate = LocalDate.of(1965, 12, 28);
        LocalDate filmDate = film.getReleaseDate();
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Пустое название фильма");
            throw new ValidationException("Название фильма пустое");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            log.debug("Описание превышает лимит символов");
            throw new ValidationException("Описание превышает лимит символов");
        }
        if (filmDate == null || filmDate.isBefore(oldDate) || filmDate.isAfter(LocalDate.now())) {
            log.debug("Дата фильма некорректна");
            throw new ValidationException("Дата фильма некорректна");
        }
        if (film.getDuration() <= 0) {
            log.debug("Продолжительность не может быть отрицательной");
            throw new ValidationException("Продолжительность не может быть отрицательной");
        }
    }

    public Film getUserById(Integer id) {
        return filmStorage.getFilmByID(id);
    }
}