package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private FilmController filmController;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldPostNewFilms() {
        Film film = new Film(null, "Имя", "Описание",
                LocalDate.of(2000, 12, 20), Duration.ofHours(2), null);
        assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/films", film, String.class))
                .contains("{\"id\":1,\"name\":\"Имя\",\"description\":\"Описание\",\"releaseDate\":\"2000-12-20\"," +
                        "\"duration\":7200.000000000,\"likedUserIdSet\":[]}");
    }

    @Test
    void shouldGetFilms() {
        Film film1 = new Film(null, "Имя2", "Описание2",
                LocalDate.of(2001, 10, 20), Duration.ofHours(3), null);
        this.restTemplate.postForLocation("http://localhost:" + port + "/films", film1);
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/films",
                String.class)).contains("[{\"id\":1,\"name\":\"Имя3\",\"description\":\"Описание3\",\"releaseDate\":" +
                "\"2002-09-20\",\"duration\":14400.000000000,\"likedUserIdSet\":[]},{\"id\":2,\"name\":\"Имя2\"," +
                "\"description\":\"Описание2\",\"releaseDate\":\"2001-10-20\",\"duration\":10800.000000000," +
                "\"likedUserIdSet\":[]}]");
    }

    @Test
    void shouldPutFilms() {
        Film film2 = new Film(1L, "Имя3", "Описание3",
                LocalDate.of(2002, 9, 20), Duration.ofHours(4), null);
        this.restTemplate.put("http://localhost:" + port + "/films", film2);
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/films",
                String.class)).contains("[{\"id\":1,\"name\":\"Имя3\",\"description\":\"Описание3\",\"releaseDate\":" +
                "\"2002-09-20\",\"duration\":14400.000000000,\"likedUserIdSet\":[]}]");
    }

    @Test
    void shouldAddLikes() {
        User user = new User(null, "email@email.com", "Login", "Name",
                LocalDate.of(2000, 1, 19), null);
        this.restTemplate.postForLocation("http://localhost:" + port + "/users", user);
        Film film = new Film(null, "Имя", "Описание",
                LocalDate.of(2000, 12, 20), Duration.ofHours(2), null);
        this.restTemplate.postForLocation("http://localhost:" + port + "/films", film);
        this.restTemplate.put("http://localhost:" + port + "/films/1/like/1", null);
        assertTrue(this.restTemplate.getForObject("http://localhost:" + port + "/films/1", Film.class)
                .getLikedUserIdSet()
                .contains(1L));
    }

    @Test
    void shouldDeleteLikes() {
        this.restTemplate.delete("http://localhost:" + port + "/films/1/like/1");
        assertFalse(this.restTemplate.getForObject("http://localhost:" + port + "/films/1", Film.class)
                .getLikedUserIdSet()
                .contains(1L));
    }
}
