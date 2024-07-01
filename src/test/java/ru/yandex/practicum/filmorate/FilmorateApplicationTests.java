package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmorateApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private FilmController filmController;

	@Autowired
	private UserController userController;

	@Autowired
	private TestRestTemplate restTemplate;


	@Test
	void shouldPostAndGetAndPutNewFilms() {
		Film film = new Film(null, "Имя", "Описание",
				LocalDate.of(2000, 12, 20), Duration.ofHours(2), null);
		Film film1 = new Film(null, "Имя2", "Описание2",
				LocalDate.of(2001, 10, 20), Duration.ofHours(3), null);
		assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/films", film, String.class))
				.contains("{\"id\":2,\"name\":\"Имя\",\"description\":\"Описание\",\"releaseDate\":\"2000-12-20\"," +
						"\"duration\":\"PT2H\",\"likedUserIdSet\":[]}");
		this.restTemplate.postForLocation("http://localhost:" + port + "/films", film1);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/films",
				String.class)).contains("[{\"id\":1,\"name\":\"Имя\",\"description\":\"Описание\",\"releaseDate\":" +
				"\"2000-12-20\",\"duration\":\"PT2H\",\"likedUserIdSet\":[]},{\"id\":2,\"name\":\"Имя\"," +
				"\"description\":\"Описание\",\"releaseDate\":\"2000-12-20\",\"duration\":\"PT2H\"," +
				"\"likedUserIdSet\":[]},{\"id\":3,\"name\":\"Имя2\",\"description\":\"Описание2\"," +
				"\"releaseDate\":\"2001-10-20\",\"duration\":\"PT3H\",\"likedUserIdSet\":[]}]");

		Film film2 = new Film(1L, "Имя3", "Описание3",
				LocalDate.of(2002, 9, 20), Duration.ofHours(4), null);
		this.restTemplate.put("http://localhost:" + port + "/films", film2);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/films",
				String.class)).contains("[{\"id\":1,\"name\":\"Имя3\",\"description\":\"Описание3\",\"releaseDate\"" +
				":\"2002-09-20\",\"duration\":\"PT4H\",\"likedUserIdSet\":[]},{\"id\":2,\"name\":\"Имя\",\"description" +
				"\":\"Описание\",\"releaseDate\":\"2000-12-20\",\"duration\":\"PT2H\",\"likedUserIdSet\":[]},{\"id\":3," +
				"\"name\":\"Имя2\",\"description\":\"Описание2\",\"releaseDate\":\"2001-10-20\",\"duration\":\"PT3H\"," +
				"\"likedUserIdSet\":[]}]");
	}

	@Test
	void shouldPostAndGetAndPutNewUsers() {
		User user = new User(null, "email@email.com", "Login", "Name",
				LocalDate.of(2000, 1, 19), null);
		User user1 = new User(null, "email@email.org", "Login1", "Name1",
				LocalDate.of(2003, 12, 10), null);
		assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/users", user, String.class))
				.contains("{\"id\":2,\"email\":\"email@email.com\",\"login\":\"Login\",\"name\":\"Name\"" +
						",\"birthday\":\"2000-01-19\",\"friendsIdSet\":[]}");
		this.restTemplate.postForLocation("http://localhost:" + port + "/users", user1);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/users",
				String.class)).contains("[{\"id\":1,\"email\":\"email@email.com\",\"login\":\"Login\",\"name\":\"Name\"," +
				"\"birthday\":\"2000-01-19\",\"friendsIdSet\":[]},{\"id\":2,\"email\":\"email@email.com\",\"login\":" +
				"\"Login\",\"name\":\"Name\",\"birthday\":\"2000-01-19\",\"friendsIdSet\":[]},{\"id\":3,\"email\":" +
				"\"email@email.org\",\"login\":\"Login1\",\"name\":\"Name1\",\"birthday\":\"2003-12-10\",\"friendsIdSet" +
				"\":[]}]");


		User user2 = new User(1L, "email@emal.ru", "NewLogin", "NewName",
				LocalDate.of(2005, 6, 6), null);
		this.restTemplate.put("http://localhost:" + port + "/users", user2);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/users",
				String.class)).contains("[{\"id\":1,\"email\":\"email@emal.ru\",\"login\":\"NewLogin\",\"name\":" +
				"\"NewName\",\"birthday\":\"2005-06-06\",\"friendsIdSet\":[]},{\"id\":2,\"email\":\"email@email.com\"," +
				"\"login\":\"Login\",\"name\":\"Name\",\"birthday\":\"2000-01-19\",\"friendsIdSet\":[]},{\"id\":3," +
				"\"email\":\"email@email.org\",\"login\":\"Login1\",\"name\":\"Name1\",\"birthday\":\"2003-12-10\"," +
				"\"friendsIdSet\":[]}]");
	}

	@Test
	void shouldAddAndGetAndDeleteLikes() {
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
		this.restTemplate.delete("http://localhost:" + port + "/films/1/like/1");
		assertFalse(this.restTemplate.getForObject("http://localhost:" + port + "/films/1", Film.class)
				.getLikedUserIdSet()
				.contains(1L));
	}

	@Test
	void shouldAddAndGetAndDeleteFriends() {
		User user = new User(null, "email@email.com", "Login", "Name",
				LocalDate.of(2000, 1, 19), null);
		User user1 = new User(null, "email@email.org", "Login1", "Name1",
				LocalDate.of(2003, 12, 10), null);
		this.restTemplate.postForLocation("http://localhost:" + port + "/users", user);
		this.restTemplate.postForLocation("http://localhost:" + port + "/users", user1);
		this.restTemplate.put("http://localhost:" + port + "/users/3/friends/4", null);
		assertTrue(this.restTemplate.getForObject("http://localhost:" + port + "/users/3", User.class)
				.getFriendsIdSet()
				.contains(4L));
		this.restTemplate.delete("http://localhost:" + port + "/users/3/friends/4");
		assertFalse(this.restTemplate.getForObject("http://localhost:" + port + "/users/3", User.class)
				.getFriendsIdSet()
				.contains(4L));

	}
}
