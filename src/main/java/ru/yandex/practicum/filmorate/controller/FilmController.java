package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        List<Film> films = filmService.getFilms();
        log.info("getFilms list size {}", films.size());
        return films;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("createFilm: film = {}", film);
        validateFilm(film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("updateFilm: film = {}", film);
        validateFilm(film);
        if (filmService.getFilmById(film.getId()) == null) {
            throw new ResourceNotFoundException("нет фильма с таким id");
        }
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("getFilm: id = {}", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("addLikeToFilm: id = {}, userId = {}", id, userId);
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("deleteLikeToFilm: id = {}, userId = {}", id, userId);
        filmService.deleteLikeToFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        log.info("getTopFilms: count = {}", count);
        return filmService.getTopFilms(count);
    }

    private static void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
    }

}
