package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Genre> genres = new HashMap<>();
    private final Map<Long, RatingMPA> ratings = new HashMap<>();
    private static final LocalDate movieBirthDate;

    static {
        //дата выхода первого кинофильма
        movieBirthDate = LocalDate.of(1895, 12, 28);
    }

    public Film find(Long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        }
        throw new NotFoundException("Пользователь id: " + filmId + " не найден");
    }

    public Collection<Film> findAll() {
        log.trace("findAll is called");
        return films.values();
    }

    public Optional<Film> create(Film film) {
        log.trace("create is called");
        validateFilm(film);
        log.debug("film passed validation");

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("new film with id = {} add into users map", film.getId());
        return Optional.of(film);
    }

    private static void validateFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.warn("name is empty");
            throw new ValidationException("Имя не может быть пустым");
        }

        if (film.getDescription().length() > 201) {
            log.warn("description is too long");
            throw new ValidationException("Длина описания не может быть больше 200 символов");
        }

        if (film.getReleaseDate().isBefore(movieBirthDate)) {
            log.warn("birthday in incorrect");
            throw new ValidationException("Минимальная дата релиза: 28.12.1895");
        }

        if (film.getDuration() < 0) {
            log.warn("duration is negative");
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }

    private Long getNextId() {
        log.trace("getNextId is called");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("generated next id = {}", currentMaxId + 1);
        return ++currentMaxId;
    }

    public Optional<Film> update(Film newFilm) {
        log.trace("update is called");
        if (newFilm.getId() == null) {
            log.warn("id is null");
            throw new NullPointerException("id не может равняться null");
        }
        if (films.containsKey(newFilm.getId())) {
            log.trace("film with id = {} found", newFilm.getId());
            validateFilm(newFilm);
            log.trace("new film passed validation");
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            return Optional.of(oldFilm);
        }
        log.warn("film with id = {} not found", newFilm.getId());
        throw new NotFoundException("Пост с id: " + newFilm.getId() + " не найден");
    }

    public boolean delete(Film film) {
        return films.remove(film.getId()).equals(film);
    }

    @Override
    public List<Film> findFilms() {
        return List.of();
    }

    @Override
    public Optional<Film> findFilmById(long filmId) {
        return Optional.empty();
    }

    public List<Genre> findGenres() {
        return genres.values().stream().toList();
    }

    @Override
    public Optional<Genre> findGenreById(long genreId) {
        return Optional.empty();
    }

    public Genre findGenreById(Long genreId) {
        return genres.get(genreId);
    }

    public List<RatingMPA> findRatingMPAs() {
        return ratings.values().stream().toList();
    }

    @Override
    public Optional<RatingMPA> findRatingMPAById(long ratingMPAId) {
        return Optional.empty();
    }

    public RatingMPA findRatingMPAById(Long ratingId) {
        return ratings.get(ratingId);
    }
}
