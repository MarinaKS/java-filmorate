package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        removeGenresDuplicate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        removeGenresDuplicate(film);
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film getFilmById(Integer id) {
        if (filmStorage.getFilmByIdOrThrow(id) == null) {
            throw new ResourceNotFoundException("нет фильма с таким id");
        }
        return filmStorage.getFilmByIdOrThrow(id);
    }

    public void addLikeToFilm(Integer filmId, Integer userId) {
        filmStorage.getFilmByIdOrThrow(filmId);
        userStorage.getUserByIdOrThrow(userId);
        filmStorage.addLikeToFilm(filmId, userId);
    }

    public void deleteLikeToFilm(Integer filmId, Integer userId) {
        filmStorage.getFilmByIdOrThrow(filmId);
        userStorage.getUserByIdOrThrow(userId);
        filmStorage.deleteLikeToFilm(filmId, userId);
    }

    public List<Film> getTopFilms(Integer count) {
        return filmStorage.getTopFilms(count);
    }

    public Genre getGenreById(int genreId) {
        return filmStorage.getGenreByIdOrThrow(genreId);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Mpa getMpaById(int mpaId) {
        return filmStorage.getMpaByIdOrThrow(mpaId);
    }

    public List<Mpa> getAllMpas() {
        return filmStorage.getAllMpas();
    }

    private static void removeGenresDuplicate(Film film) {
        List<Genre> genresWithoutDuplicate = new ArrayList<>(new LinkedHashSet<>(film.getGenres()));
        film.setGenres(genresWithoutDuplicate);
    }
}
