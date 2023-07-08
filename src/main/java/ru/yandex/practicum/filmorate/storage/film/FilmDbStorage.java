package ru.yandex.practicum.filmorate.storage.film;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ArrayList<Film> getFilms() {
        List<Film> filmQuery = jdbcTemplate.query("SELECT f.ID, " +
                "f.NAME, " +
                "f.DESCRIPTION, " +
                "f.DURATION, " +
                "f.MPA_ID, " +
                "f.RELEASE_DATE, " +
                "m.NAME mpa_name, " +
                "g.NAME genre_name," +
                "fg.GENRE_ID " +
                "FROM films f JOIN mpa m on f.mpa_id = m.id " +
                "LEFT JOIN films_genres fg on f.id = fg.film_id " +
                "LEFT JOIN genres g on g.id = fg.genre_id " +
                "GROUP BY f.ID, genre_name " +
                "ORDER BY fg.GENRE_ID DESC ", rowMapperFilm());
        return getFilmsFromQuery(filmQuery);
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        Map<String, String> params = Map.of("name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate().toString(),
                "duration", String.valueOf(film.getDuration()),
                "mpa_id", film.getMpa().toString());
        Integer id = (Integer) insert.executeAndReturnKey(params);
        film.setId(id);
        addGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        int update;
        update = jdbcTemplate.update(" UPDATE FILMS SET NAME = ?," +
                        " DESCRIPTION = ?, " +
                        "DURATION = ?, " +
                        "MPA_ID = ?, " +
                        "RELEASE_DATE = ? " +
                        "WHERE id = ?;",
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().toString(),
                film.getReleaseDate(),
                film.getId());
        if (update == 0) {
            throw new NotFoundException("Такого фильма нет");
        }
        updateGenres(film);
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        jdbcTemplate.update("delete from films where id = ?", film.getId());
    }

    @Override
    public Film getFilmByID(int id) {
        List<Film> films = jdbcTemplate.query("SELECT f.ID, " +
                        "f.NAME, " +
                        "f.DESCRIPTION, " +
                        "f.DURATION, " +
                        "f.MPA_ID, " +
                        "f.RELEASE_DATE, " +
                        "m.NAME mpa_name, " +
                        "fg.GENRE_ID, " +
                        "g.NAME genre_name " +
                        "FROM films f JOIN mpa m on f.mpa_id = m.id " +
                        "LEFT JOIN films_genres fg on f.id = fg.film_id " +
                        "LEFT JOIN genres g on fg.genre_id = g.id " +
                        "where f.ID = ? " +
                        "GROUP BY f.ID, fg.GENRE_ID " +
                        "ORDER BY fg.GENRE_ID;",
                rowMapperFilm(), id);
        if (films.size() == 0) {
            throw new NotFoundException("Такого фильма нет");
        }
        return getFilmsFromQuery(films).get(0);
    }

    @Override
    public ArrayList<Film> getPopularFilms(Integer count) {
        if (count == null || count <= 0) {
            count = 10;
        }
        List<Film> films = jdbcTemplate.query("SELECT f.ID, " +
                        "f.NAME , " +
                        "f.DESCRIPTION, " +
                        "f.DURATION, " +
                        "f.MPA_ID, " +
                        "m.NAME mpa_name, " +
                        "g.NAME genre_name, " +
                        "fg.GENRE_ID, " +
                        "f.RELEASE_DATE, " +
                        "COUNT(l.USER_ID) likes_count " +
                        "FROM FILMS f " +
                        "JOIN mpa m on f.mpa_id = m.id " +
                        "LEFT JOIN LIKES l ON f.id = l.FILM_ID " +
                        "LEFT JOIN films_genres fg on f.id = fg.film_id " +
                        "LEFT JOIN genres g on fg.genre_id = g.id " +
                        "GROUP BY f.ID, genre_name " +
                        "ORDER BY likes_count DESC " +
                        "LIMIT ?;",
                rowMapperFilm(), count);
        return (ArrayList<Film>) films;
    }

    @Override
    public void addLike(int filmID, int userID) {
        int update = jdbcTemplate.update("INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)", filmID, userID);
        if (update == 0) {
            throw new NotFoundException("Такого фильма нет");
        }
    }

    @Override
    public void deleteLike(int filmID, int userID) {
        int update = jdbcTemplate.update("DELETE FROM LIKES WHERE FILM_ID = " + filmID + " AND USER_ID = " + userID + ";");
        if (update == 0) {
            throw new NotFoundException("Такого фильма нет");
        }
    }

    @Override
    public ArrayList<Genre> getGenres() {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM GENRES ORDER BY ID",
                (rs, rowNum) -> new Genre(rs.getInt("ID"), rs.getString("NAME")));
        return (ArrayList<Genre>) genres;
    }

    @Override
    public Genre getGenresById(Integer id) {
        List<Genre> genres = jdbcTemplate.query("SELECT * FROM GENRES WHERE ID = ?",
                (rs, rowNum) -> new Genre(rs.getInt("ID"), rs.getString("NAME")), id);
        if (genres.size() == 0) {
            throw new NotFoundException("Такого жанра нет");
        }
        return genres.get(0);
    }

    @Override
    public ArrayList<Mpa> getMpaList() {
        List<Mpa> mpaList = jdbcTemplate.query("SELECT * FROM MPA ORDER BY ID",
                (rs, rowNum) -> new Mpa(rs.getInt("ID"), rs.getString("NAME")));
        return (ArrayList<Mpa>) mpaList;
    }

    @Override
    public Mpa getMpaById(Integer id) {
        List<Mpa> mpaList = jdbcTemplate.query("SELECT * FROM MPA WHERE ID = ?",
                (rs, rowNum) -> new Mpa(rs.getInt("ID"), rs.getString("NAME")), id);
        if (mpaList.size() == 0) {
            throw new NotFoundException("Такого рейтинга нет");
        }
        return mpaList.get(0);
    }

    public void deleteFilms() {
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM LIKES");
    }

    private RowMapper<Film> rowMapperFilm() {
        return new RowMapper<Film>() {
            @Override
            public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
                Film film = new Film();
                film.setId(rs.getInt("id"));
                film.setName(rs.getString("name"));
                film.setDescription(rs.getString("description"));
                film.setDuration(rs.getLong("duration"));
                film.setReleaseDate(rs.getDate("release_date").toLocalDate());
                film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
                if (rs.getInt("genre_id") > 0) {
                    film.setGenres(FilmDbStorage.this.newGenre(rs, film));
                } else film.setGenres(new ArrayList<>());
                return film;
            }
        };
    }

    @SneakyThrows
    private List<Genre> newGenre(ResultSet rs, Film film) {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
        return genres;
    }

    private void updateGenres(Film film) {
        if (film.getGenres() != null) {
            String sql = "DELETE FROM FILMS_GENRES WHERE FILM_ID = " + film.getId();
            jdbcTemplate.update(sql);
            if (film.getGenres().size() > 0) {
                List<Genre> genres = new ArrayList<>();
                for (Genre genre : film.getGenres()) {
                    if (!genres.contains(genre)) {
                        genres.add(genre);
                    }
                }
                film.setGenres(genres);
                int i = genres.size();
                sql = "MERGE INTO FILMS_GENRES (film_id, GENRE_ID) VALUES ";
                for (Genre genre : genres) {
                    sql += "(" + film.getId() + ", " + genre.getId() + ")";
                    --i;
                    if (i > 0) {
                        sql += ", ";

                    }
                }
                jdbcTemplate.update(sql);
            }
        }
    }

    private void addGenres(Film film) {
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            List<Genre> genres = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                if (!genres.contains(genre)) {
                    genres.add(genre);
                }
            }
            int i = genres.size();
            String sql = "INSERT INTO FILMS_GENRES (film_id, GENRE_ID) VALUES ";
            for (Genre genre : genres) {
                sql += "(" + film.getId() + ", " + genre.getId() + ")";
                --i;
                if (i > 0) {
                    sql += ", ";
                }
            }
            film.setGenres(genres);
            jdbcTemplate.update(sql);
        }
    }

    private ArrayList<Film> getFilmsFromQuery(List<Film> filmQuery) {
        ArrayList<Film> films = new ArrayList<>();
        if (filmQuery.size() > 0) {
            Film currentFilm = filmQuery.get(0);
            List<Genre> genres = new ArrayList<>();
            for (Film film : filmQuery) {

                if (film.getId() != currentFilm.getId()) {
                    films.add(currentFilm);
                    currentFilm = film;
                    genres.clear();
                }

                if (!genres.containsAll(film.getGenres())) {
                    genres.addAll(film.getGenres());
                }

            }

            currentFilm.setGenres(genres);
            films.add(currentFilm);
        }
        return films;
    }

}
