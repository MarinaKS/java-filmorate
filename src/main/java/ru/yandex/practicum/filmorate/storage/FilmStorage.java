package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface FilmStorage {
    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public void deleteFilm(Integer id);

    public List<Film> getFilms();

    public Film getFilmByIdOrThrow(Integer id);

    void addLikeToFilm(Integer filmId, Integer userId);

    void deleteLikeToFilm(Integer filmId, Integer userId);
    List<Film> getTopFilms(Integer count);
    public Genre getGenreByIdOrThrow(int genreId);
    public List<Genre> getAllGenres();
    public Mpa getMpaByIdOrThrow(int mpaId);
    public List<Mpa> getAllMpas();
}
