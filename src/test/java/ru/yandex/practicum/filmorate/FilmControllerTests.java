package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class FilmControllerTests {
    private final FilmStorage filmStorage = new InMemoryFilmStorage();
    private final FilmService filmService = new FilmService(filmStorage);
    private final FilmController filmController = new FilmController(filmService);

    @Test
    void addNewFilm() throws ValidationException {
        assertTrue(filmController.getFilms().isEmpty());
        Film film = Film.builder()
                .description("test description")
                .name("test film")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .duration(1000000)
                .build();
        filmController.add(film);
        assertTrue(filmStorage.getFilms().contains(filmStorage.getFilmByID(1)));
    }

    @Test
    void addNewFilmWithoutName() {
        Film film = Film.builder()
                .description("test description")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .duration(1000000)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setName(" ");
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void addNewFilmWithIncorrectDescription() {
        Film film = Film.builder()
                .name("test film")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .duration(1000000)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setDescription("somedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescrip" +
                "somedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescrip" +
                "somedescripsomedescripsomedescripsomedescrip");
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void addNewFilmWithIncorrectDuration() {
        Film film = Film.builder()
                .description("test description")
                .name("test film")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .build();
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setDuration(-1);
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void addNewFilmWithIncorrectReleaseDate() {
        Film film = Film.builder()
                .description("test description")
                .name("test film")
                .duration(1000000)
                .build();
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setReleaseDate(LocalDate.of(2100, 3, 5));
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setReleaseDate(LocalDate.of(1950, 11, 5));
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void updateFilm() throws ValidationException {
        Film film = Film.builder()
                .id(1)
                .description("test description")
                .name("test film")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .duration(1000000)
                .build();
        filmController.add(film);
        filmController.update(film);
    }

    @Test
    void updateFilmWithIncorrectID() throws NotFoundException {
        Film film = Film.builder()
                .id(0)
                .description("test description")
                .name("test film")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .duration(1000000)
                .build();
        filmController.add(film);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(film));
    }

}
