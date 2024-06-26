package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate movieBirthDate;

    static {
        //дата выхода первого кино
        movieBirthDate = LocalDate.of(1895, 12, 28);
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.trace("findAll is called");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.trace("create is called");
        validateFilm(film);
        log.debug("film passed validation");

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("new film with id = {} add into users map", film.getId());
        return film;
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

        if (film.getDuration().isNegative()) {
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

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
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
            return oldFilm;
        }
        log.warn("film with id = {} not found", newFilm.getId());
        throw new NotFoundException("Пост с id: " + newFilm.getId() + " не найден");
    }
}
