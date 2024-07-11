package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping
@Validated
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    @PostMapping("/films")
    @Validated
    public Film create(@Valid @RequestBody Film film) {
        film = ValidatorControllers.validateFilm(film);
        Film newFilm = filmService.create(film);
        return newFilm;
    }

    @PutMapping("/films")
    @Validated
    public Film update(@Valid @RequestBody Film film) {
        ValidatorControllers.validateFilm(film);
        Film newFilm = filmService.update(film);
        return newFilm;
    }

    @DeleteMapping("/films")
    @Validated
    public void delete(@Valid @RequestBody Film film) {
        filmService.delete(film);
    }

    @GetMapping("/films")
    public List<Film> findFilms() {
        List<Film> films = filmService.findFilms();
        return films;
    }

    @GetMapping("/films/{filmId}")
    public Film findFilmById(@PathVariable long filmId) {
        Film film = filmService.findFilmById(filmId);
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public boolean like(@PathVariable long id, @PathVariable long userId) {
        if (filmService.like(id, userId)) {
            return true;
        }
        return false;
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public boolean dislike(@PathVariable long id, @PathVariable long userId) {
        if (filmService.dislike(id, userId)) {
            return true;
        }
        return false;
    }

    @GetMapping("/films/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") String count) {
        int countInt = Integer.parseInt(count);
        if (countInt < 0) {
            String message = "Параметр count не может быть отрицательным!";
            throw new ValidationException(message);
        }
        List<Film> films = filmService.findPopularFilms(countInt);
        return films;
    }

    @GetMapping("/genres")
    public List<Genre> findGenres() {
        List<Genre> genres = filmService.findGenres();
        return genres;
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable long id) {
        Genre genre = filmService.findGenreById(id);
        return genre;
    }

    @GetMapping("/mpa")
    public List<RatingMPA> findRatingMPAs() {
        List<RatingMPA> ratingMPAs = filmService.findRatingMPAs();
        return ratingMPAs;
    }

    @GetMapping("/mpa/{id}")
    public RatingMPA findRatingMPAById(@PathVariable long id) {
        RatingMPA ratingMPA = filmService.findRatingMPAById(id);
        return ratingMPA;
    }

}
