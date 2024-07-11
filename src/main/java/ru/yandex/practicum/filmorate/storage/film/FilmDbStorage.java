package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public Film create(Film film) {
        String sqlQuery = "insert into films(film_name, description, release_date, duration, rate, rating_mpa_id) " +
                " values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setLong(6, film.getMpa().getId());

            return stmt;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());
        film.setMpa(findRatingMPAById(film.getMpa().getId()));
        Film updateFilm = updateGenres(film);
        return updateFilm;
    }

    public Film update(Film film) {
        String sqlQuery = "update films set film_name = ?, description = ?, release_date = ?, " +
                "duration = ?, rate = ?, rating_mpa_id = ? where film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());

        film.setMpa(findRatingMPAById(film.getMpa().getId()));
        deleteGenres(film.getId());
        film = updateGenres(film);
        return film;
    }

    public boolean delete(Film film) {
        if (find(film.getId()) == null) {
            return false;
        }
        String sqlQuery = "delete from films where film_id = ?";
        return jdbcTemplate.update(sqlQuery, film.getId()) > 0;
    }

    public List<Film> findAll() {
        String sqlQuery = "select * from films";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    public Film find(Long filmId) {
        String sqlQuery = "select * from films where film_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId);
    }

    public List<Genre> findGenres() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    public Genre findGenre(Long genreId) {
        String sqlQuery = "select * from genres where genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, genreId);
    }

    public List<RatingMPA> findRatingMPAs() {
        String sqlQuery = "select * from rating_mpa";
        return jdbcTemplate.query(sqlQuery, this::makeRatingMPA);
    }

    public RatingMPA findRatingMPAById(long ratingMPAId) {
        String sqlQuery = "select * from rating_mpa where rating_mpa_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeRatingMPA, ratingMPAId);
    }

    private void createGenre(Long filmId, Genre genre) {
        String sqlQuery = "insert into film_genre(film_id, genre_id) " +
                " values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, genre.getId());
    }

    private Film updateGenres(Film film) {
        film.setGenres(film.getGenres().stream()
                .map(genre -> findGenreById(genre.getId()))
                .collect(Collectors.toSet()));

        for (Genre genre : film.getGenres()) {
            createGenre(film.getId(), genre);
        }
        return film;
    }

    private void deleteGenres(Long filmId) {
        String sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private List<Genre> findGenreByFilmId(Long filmId) {
        String sqlQuery = "select * from film_genre as fg " +
                "join genres as g on g.genre_id = fg.genre_id " +
                "where fg.film_id = ? order by genre_id";
        return jdbcTemplate.query(sqlQuery, this::makeGenre, filmId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film(rs.getString("film_name"), rs.getString("description"),
                rs.getDate("release_date").toLocalDate());
        film.setId(rs.getLong("film_id"));
        film.setDuration(rs.getInt("duration"));
        film.setRate(rs.getInt("rate"));
        film.setGenres(new HashSet<>(findGenreByFilmId(film.getId())));
        film.setMpa(findRatingMPAById(rs.getLong("rating_mpa_id")));
        return film;
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
    }

    private RatingMPA makeRatingMPA(ResultSet rs, int rowNum) throws SQLException {
        return new RatingMPA(rs.getLong("rating_mpa_id"),
                rs.getString("rating_mpa_name"), rs.getString("description"));
    }

}
