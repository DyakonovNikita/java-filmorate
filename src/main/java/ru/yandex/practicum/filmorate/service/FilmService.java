package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.likeStorage.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmRepository")
    private final FilmStorage filmStorage;
    @Qualifier("userRepository")
    private final UserStorage userStorage;
    @Qualifier("likeRepository")
    private final LikeStorage likeStorage;


    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (findFilmById(film.getId()) == null) {
            return null;
        }
        return filmStorage.update(film);
    }

    public boolean delete(Film film) {
        return filmStorage.delete(film);
    }

    public List<Film> findFilms() {
        return filmStorage.findAll().stream()
                .peek(film -> film.setLikes(new HashSet<>(likeStorage.findLikes(film))))
                .collect(Collectors.toList());
    }

    public Film findFilmById(long filmId) {
        Film film = filmStorage.find(filmId);
        film.setLikes(new HashSet<>(likeStorage.findLikes(film)));
        return film;
    }

    public boolean like(long id, long userId) {
        if (findFilmById(id) == null || userStorage.findUserById(userId).isEmpty()) {
            return false;
        }

        Film film = findFilmById(id);
        film.getLikes().add(userId);
        likeStorage.dislike(film);
        likeStorage.like(film);
        return true;
    }

    public boolean dislike(long id, long userId) {
        if (findFilmById(id) == null || userStorage.findUserById(userId).isEmpty()) {
            return false;
        }

        Film film = findFilmById(id);
        film.getLikes().remove(userId);
        likeStorage.dislike(film);
        likeStorage.like(film);
        return true;
    }

    public List<Film> findPopularFilms(int count) {
        return findFilms().stream()
            .sorted(this::compare)
            .limit(count)
            .collect(Collectors.toList());
    }

    public List<Genre> findGenres() {
        return filmStorage.findGenres();
    }

    public Genre findGenreById(long genreId) {
        return filmStorage.(genreId);
    }

    public List<RatingMPA> findRatingMPAs() {
        return filmStorage.findRatingMPAs();
    }

    public RatingMPA findRatingMPAById(long ratingMPAId) {
        return filmStorage.findRatingMPAById(ratingMPAId);
    }

    private int compare(Film film1, Film film2) {
        return film2.getLikes().size() - film1.getLikes().size();
    }
}