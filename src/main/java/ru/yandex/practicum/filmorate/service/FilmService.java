package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;


    public Film find(Long filmId) {
        return filmStorage.find(filmId);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film newFilm) {
        return filmStorage.create(newFilm);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public void likeFilm(Long filmId, Long userId) {
        Film film = filmStorage.find(filmId);
        userService.find(userId);
        Set<Long> filmLikesSet = film.getLikedUserIdSet();
        filmLikesSet.add(userId);
        film.setLikedUserIdSet(filmLikesSet);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmStorage.find(filmId);
        userService.find(userId);
        Set<Long> filmLikesSet = film.getLikedUserIdSet();
        filmLikesSet.remove(userId);
        film.setLikedUserIdSet(filmLikesSet);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikedUserIdSet().size()))
                .limit(count)
                .collect(Collectors.toList())
                .reversed();
    }
}
