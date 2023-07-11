package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final FilmController filmController;
    private final MpaController mpaController;
    private final GenreController genreController;

    private final UserDbStorage userStorage;
    private final UserController userController;
    Film film;

    @BeforeEach
    void newFilm() {
        film = Film.builder()
                .id(1)
                .description("test description")
                .name("test film")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .duration(1000000)
                .mpa(new Mpa(1, "G"))
                .build();
    }

    @AfterEach
    void afterEach() {
        filmStorage.deleteFilms();
        userStorage.deleteUsers();
    }

    @Test
    void addNewFilm() throws ValidationException {
        assertTrue(filmStorage.getFilms().isEmpty());
        filmController.add(film);
        assertFalse(filmController.getFilms().isEmpty());
    }

    @Test
    void addNewFilmWithoutName() {
        film.setName("");
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setName(" ");
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void addNewFilmWithIncorrectDescription() {
        film.setDescription(null);
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setDescription("somedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescrip" +
                "somedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescripsomedescrip" +
                "somedescripsomedescripsomedescripsomedescrip");
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void addNewFilmWithIncorrectDuration() {
        film.setDuration(0);
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setDuration(-1);
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void addNewFilmWithIncorrectReleaseDate() {
        film.setReleaseDate(null);
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setReleaseDate(LocalDate.of(2100, 3, 5));
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
        film.setReleaseDate(LocalDate.of(1950, 11, 5));
        Assertions.assertThrows(ValidationException.class, () -> filmController.add(film));
    }

    @Test
    void updateFilm() throws ValidationException {
        Film testFilm = filmController.add(film);
        Film updateFilm = filmController.update(film);
        assertTrue(testFilm.equals(updateFilm));
    }

    @Test
    void updateFilmWithIncorrectID() throws NotFoundException {
        filmController.add(film);
        film.setId(0);
        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(film));
    }

    @Test
    void getPopularFilmsWithCorrectCount() {
        ArrayList<Film> filmArrayList = new ArrayList<>();
        filmArrayList.add(film);
        assertTrue(filmArrayList.containsAll(filmController.getMostPopularFilms(3)));
    }

    @Test
    void getPopularFilmsWithIncorrectCount() {
        ArrayList<Film> filmArrayList = new ArrayList<>();
        filmArrayList.add(film);
        assertTrue(filmArrayList.containsAll(filmController.getMostPopularFilms(-3)));
    }

    @Test
    void addLikeToFilm() {
        User user = User.builder()
                .id(1)
                .email("email@mail")
                .login("login")
                .birthday(LocalDate.of(2000, 12, 17))
                .build();
        filmController.add(film);
        userController.add(user);
        filmController.addLike(film.getId(), user.getId());
    }

    @Test
    void getMostPopularWithLikes() {
        User user = User.builder()
                .id(0)
                .email("email@mail")
                .login("login")
                .birthday(LocalDate.of(2000, 12, 17))
                .build();
        Film film2 = Film.builder()
                .description("test description2")
                .name("test film2")
                .releaseDate(LocalDate.of(2006, 2, 12))
                .duration(1000000)
                .mpa(new Mpa(2, "PG"))
                .build();
        filmController.add(film);
        filmController.add(film2);
        userController.add(user);
        ArrayList<Film> mostPopular = new ArrayList<>();
        filmController.addLike(2, 2);
        film = filmController.getFilmById(2);
        mostPopular.add(film);
        assertEquals(mostPopular, filmController.getMostPopularFilms(1));
    }

    @Test
    void deleteLikeToFilmWithIncorectId() {
        User user = User.builder()
                .id(1)
                .email("email@mail")
                .login("login")
                .birthday(LocalDate.of(2000, 12, 17))
                .build();
        filmController.add(film);
        userController.add(user);
        assertThrows(NotFoundException.class, () -> filmController.deleteLike(99, 1));
        film = filmController.getMostPopularFilms(1).get(0);
        assertEquals(0, film.getLikes());
    }

    @Test
    void deleteLikeToFilmWithCorectId() {
        User user = User.builder()
                .id(1)
                .email("email@mail")
                .login("login")
                .birthday(LocalDate.of(2000, 12, 17))
                .build();
        filmController.add(film);
        userController.add(user);
        filmController.addLike(1, 1);
        filmController.deleteLike(1, 1);
        film = filmController.getFilmById(1);
        assertEquals(0, film.getLikes());
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = genreController.getGenres();
        List<Genre> genreList = new ArrayList<>();
        genreList.addAll(List.of(new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"),
                new Genre(4, "Триллер"),
                new Genre(5, "Документальный"),
                new Genre(6, "Боевик")));
        assertArrayEquals(new List[]{genreList}, new List[]{genres});
    }

    @Test
    void getGenreByIdWithCorectId() {
        Genre genre = genreController.getGenresById(3);
        Genre genre3 = new Genre(3, "Мультфильм");
        assertEquals(genre, genre3);
    }

    @Test
    void getGenreByIdWithIncorectId() {
        assertThrows(NotFoundException.class, () -> genreController.getGenresById(99));
    }

    @Test
    void getAllMpa() {
        List<Mpa> rates = mpaController.getMpaList();
        List<Mpa> mpaList = new ArrayList<>();
        mpaList.addAll(List.of(new Mpa(1, "G"),
                new Mpa(2, "PG"),
                new Mpa(3, "PG-13"),
                new Mpa(4, "R"),
                new Mpa(5, "NC-17")));
        assertArrayEquals(new List[]{rates}, new List[]{mpaList});
    }

    @Test
    void getMpaByIdWithCorectId() {
        Mpa mpa = mpaController.getMpaById(5);
        Mpa mpa5 = new Mpa(5, "NC-17");
        assertEquals(mpa, mpa5);
    }

    @Test
    void getMpaByIdWithIncorectId() {
        assertThrows(NotFoundException.class, () -> mpaController.getMpaById(99));
    }

}