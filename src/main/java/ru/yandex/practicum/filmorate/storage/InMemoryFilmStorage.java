package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @Override
    public Film createFilm(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        return films.get(id);
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public void deleteFilm(Integer id) {
        films.remove(id);
    }

    @Override
    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmByIdOrThrow(Integer id) {
        if (films.get(id) == null) {
            throw new ResourceNotFoundException("нет фильма с таким id");
        }
        return films.get(id);
    }

    @Override
    public void addLikeToFilm(Integer filmId, Integer userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteLikeToFilm(Integer filmId, Integer userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> getTopFilms(Integer count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Genre getGenreByIdOrThrow(int genreId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Genre> getAllGenres() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Mpa getMpaByIdOrThrow(int mpaId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Mpa> getAllMpas() {
        throw new UnsupportedOperationException();
    }
}
