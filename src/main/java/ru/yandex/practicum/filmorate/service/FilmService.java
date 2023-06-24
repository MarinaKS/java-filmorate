package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }


    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }


    public void deleteFilm(Integer id) {
        filmStorage.deleteFilm(id);
    }


    public ArrayList<Film> getFilms() {
        return filmStorage.getFilms();
    }


    public Film getFilmById(Integer id) {
        if (filmStorage.getFilmById(id) == null) {
            throw new ResourceNotFoundException("нет фильма с таким id");
        }
        return filmStorage.getFilmById(id);
    }

    public boolean addLikeToFilm(Integer filmId, Integer userId) {
        userStorage.getUserByIdOrThrow(userId);
        filmStorage.getFilmById(filmId).getLikedUserIds().add(userId);
        return true;
    }

    public boolean deleteLikeToFilm(Integer filmId, Integer userId) {
        userStorage.getUserByIdOrThrow(userId);
        filmStorage.getFilmById(filmId).getLikedUserIds().remove(userId);
        return true;
    }

    public List<Film> getTopFilms(Integer count) {
        List<Film> sortedListFilms = filmStorage.getFilms();
        sortedListFilms.sort(Comparator.comparing(x -> -x.getLikedUserIds().size()));
        if (sortedListFilms.size() < count) {
            return sortedListFilms;
        }
        return sortedListFilms.subList(0, count);
    }
}
