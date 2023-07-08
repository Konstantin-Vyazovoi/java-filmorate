package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.ArrayList;

@RestController
@RequestMapping
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public ArrayList<Film> getFilms() {
        return filmService.getFilms();
    }

    @PostMapping("/films")
    public Film add(@RequestBody Film film) throws ValidationException {
        return filmService.add(film);
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) throws ValidationException {
        return filmService.update(film);
    }

    @GetMapping("/films/{id}")
    public Film getUserById(@PathVariable("id") Integer id) {
        return filmService.getFilmById(id);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer id,
                        @PathVariable("userId") Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void deletLike(@PathVariable("id") Integer id,
                          @PathVariable("userId") Integer userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/films/popular")
    public ArrayList<Film> getMostPopularFilms(@RequestParam(required = false) Integer count) {

        return filmService.getPopularFilms(count);
    }

    @GetMapping("/genres")
    public ArrayList<Genre> getGenres() {
        return filmService.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenresById(@PathVariable("id") Integer id) {
        return filmService.getGenresById(id);
    }

    @GetMapping("/mpa")
    public ArrayList<Mpa> getMpaList() {
        return filmService.getMpaList();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable("id") Integer id) {
        return filmService.getMpaById(id);
    }
}
