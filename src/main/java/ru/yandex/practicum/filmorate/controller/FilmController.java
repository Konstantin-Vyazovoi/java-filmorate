package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.excption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int id = 0;

    @GetMapping("/films")
    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @GetMapping("/films/{id}")
    public Film getFilmById() {
        return films.get(id);
    }

    @PostMapping("/films")
    public Film add(@RequestBody Film film) throws ValidationException {
        validation(film);
        Film newFilm;
        if (film.getId() == 0) {
            newFilm = Film.builder()
                    .id(generateID())
                    .name(film.getName())
                    .description(film.getDescription())
                    .duration(film.getDuration())
                    .releaseDate(film.getReleaseDate())
                    .build();
        } else newFilm = film;
        films.put(newFilm.getId(), newFilm);
        log.trace("Добавлен новый фильм");
        return newFilm;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) throws ValidationException {

        validation(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            log.debug("Такого фильма нет");
            throw new ValidationException("Такого фильма нет");
        }
        log.trace("Фильм с id {} обновлен", film.getId());
        return film;
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

    public HashMap<Integer, Film> getFilmMap() {
        return films;
    }

    private int generateID() {
        id++;
        return id;
    }
}
