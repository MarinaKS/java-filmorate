package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Primary
@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "insert into films (name, description, release_date, duration, mpa_id) " +
                "values (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"FILM_ID"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, (java.sql.Date.valueOf(film.getReleaseDate())));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "update films set name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "mpa_id = ? " +
                "where film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        updateFilmGenres(film);
        return film;
    }

    @Override
    public void deleteFilm(Integer id) {
        String sql = "delete from films where film_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select film_id, name, description, release_date, duration, mpa_id from films";
        return jdbcTemplate.query(sql, this::mapFilm);
    }

    @Override
    public Film getFilmByIdOrThrow(Integer id) {
        String sql = "select film_id, name, description, release_date, duration, mpa_id from films where film_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Фильм с таким id не найден.");
        }
    }

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        String sql = "insert into likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLikeToFilm(Integer filmId, Integer userId) {
        String sql = "delete from likes where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        String sql = "select *, (select count(user_id) from likes l where l.film_id = f.film_id) as film_likes " +
                "from films as f " +
                "order by film_likes desc " +
                "limit ?";
        return jdbcTemplate.query(sql, this::mapFilm, count);
    }

    @Override
    public Genre getGenreByIdOrThrow(int genreId) {
        String sql = "select genre_id, name from genres where genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapGenre, genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Жанр с таким id не найден.");
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "select genre_id, name from genres";
        return jdbcTemplate.query(sql, this::mapGenre);
    }

    @Override
    public Mpa getMpaByIdOrThrow(int mpaId) {
        String sql = "select mpa_id, name from mpas where mpa_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapMpa, mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Рейтинг с таким id не найден.");
        }
    }

    @Override
    public List<Mpa> getAllMpas() {
        String sql = "select mpa_id, name from mpas";
        return jdbcTemplate.query(sql, this::mapMpa);
    }

    public List<Genre> getGenresByFilmId(Integer filmId) {
        String sql = "select genre_id, name from genres " +
                "where genre_id in (select genre_id from film_genres where film_id = ?)";
        return jdbcTemplate.query(sql, this::mapGenre, filmId);
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update("delete from film_genres where film_id = ?", film.getId());
        jdbcTemplate.batchUpdate("insert into film_genres(film_id, genre_id) values (?,?)", film.getGenres(),
                film.getGenres().size(), (ps, genre) -> {
                    ps.setInt(1, film.getId());
                    ps.setInt(2, genre.getId());
                });
    }

    private Film mapFilm(ResultSet rows, int rowNum) throws SQLException {
        int filmId = rows.getInt("film_id");
        Film film = new Film(
                filmId,
                rows.getString("name"),
                rows.getString("description"),
                Objects.requireNonNull(rows.getDate("release_date")).toLocalDate(),
                rows.getInt("duration"));

        int mpaId = rows.getInt("mpa_id");
        film.setMpa(getMpaByIdOrThrow(mpaId));

        film.setGenres(getGenresByFilmId(filmId));
        return film;
    }

    private Genre mapGenre(ResultSet rows, int rowNum) throws SQLException {
        return new Genre(
                rows.getInt("genre_id"),
                rows.getString("name"));
    }

    private Mpa mapMpa(ResultSet rows, int rowNum) throws SQLException {
        return new Mpa(
                rows.getInt("mpa_id"),
                rows.getString("name"));
    }
}
