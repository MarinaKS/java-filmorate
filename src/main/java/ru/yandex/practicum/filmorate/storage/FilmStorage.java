package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmStorage {
    public Film createFilm(Film film);

    public Film updateFilm(Film film);

    public void deleteFilm(Integer id);

    public ArrayList<Film> getFilms();

    public Film getFilmById(Integer id);
}
