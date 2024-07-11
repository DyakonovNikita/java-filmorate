package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film find(Long filmId);

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    boolean delete(Film film);

    List<Genre> findGenres();

    List<Genre> findGenreById(Long genreId);

    List<RatingMPA> findRatingMPAs();

    RatingMPA findRatingMPAById(Long ratingId);
}
