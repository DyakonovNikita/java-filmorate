package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTests {

	@LocalServerPort
	private int port;

	@Autowired
	private UserController userController;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	void shouldPostNewUsers() {
		User user = new User(null, "email@email.com", "Login", "Name",
				LocalDate.of(2000, 1, 19), null);
		assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/users", user, String.class))
				.contains("{\"id\":1,\"email\":\"email@email.com\",\"login\":\"Login\",\"name\":\"Name\"" +
						",\"birthday\":\"2000-01-19\",\"friendsIdSet\":[]}");
	}

	@Test
	void shouldGetUsers() {
		User user1 = new User(null, "email@email.org", "Login1", "Name1",
				LocalDate.of(2003, 12, 10), null);
		this.restTemplate.postForLocation("http://localhost:" + port + "/users", user1);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/users",
				String.class)).contains("[{\"id\":1,\"email\":\"email@email.com\",\"login\":\"Login\",\"name\":\"Name\"," +
				"\"birthday\":\"2000-01-19\",\"friendsIdSet\":[2]},{\"id\":2,\"email\":\"email@email.com\",\"login\":" +
				"\"Login\",\"name\":\"Name\",\"birthday\":\"2000-01-19\",\"friendsIdSet\":[1]},{\"id\":3,\"email\":" +
				"\"email@email.org\",\"login\":\"Login1\",\"name\":\"Name1\",\"birthday\":\"2003-12-10\"," +
				"\"friendsIdSet\":[]},{\"id\":4,\"email\":\"email@email.org\",\"login\":\"Login1\",\"name\":\"Name1\"," +
				"\"birthday\":\"2003-12-10\",\"friendsIdSet\":[]}]");
	}

	@Test
	void shouldPutNewUsers() {
		User user2 = new User(1L, "email@emal.ru", "NewLogin", "NewName",
				LocalDate.of(2005, 6, 6), null);
		this.restTemplate.put("http://localhost:" + port + "/users", user2);
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/users",
				String.class)).contains("[{\"id\":1,\"email\":\"email@emal.ru\",\"login\":\"NewLogin\",\"name\":" +
				"\"NewName\",\"birthday\":\"2005-06-06\",\"friendsIdSet\":[2]},{\"id\":2,\"email\":\"email@email.com\"," +
				"\"login\":\"Login\",\"name\":\"Name\",\"birthday\":\"2000-01-19\",\"friendsIdSet\":[1]},{\"id\":3," +
				"\"email\":\"email@email.org\",\"login\":\"Login1\",\"name\":\"Name1\",\"birthday\":\"2003-12-10\"," +
				"\"friendsIdSet\":[]},{\"id\":4,\"email\":\"email@email.org\",\"login\":\"Login1\",\"name\":\"Name1\"," +
				"\"birthday\":\"2003-12-10\",\"friendsIdSet\":[]}]");
	}

	@Test
	void shouldAddFriends() {
		User user = new User(null, "email@email.com", "Login", "Name",
				LocalDate.of(2000, 1, 19), null);
		User user1 = new User(null, "email@email.org", "Login1", "Name1",
				LocalDate.of(2003, 12, 10), null);
		this.restTemplate.postForLocation("http://localhost:" + port + "/users", user);
		this.restTemplate.postForLocation("http://localhost:" + port + "/users", user1);
		this.restTemplate.put("http://localhost:" + port + "/users/1/friends/2", null);
		assertTrue(this.restTemplate.getForObject("http://localhost:" + port + "/users/1", User.class)
				.getFriendsIdSet()
				.contains(2L));
	}

	@Test
	void shouldDeleteFriends() {
		this.restTemplate.delete("http://localhost:" + port + "/users/1/friends/2");
		assertFalse(this.restTemplate.getForObject("http://localhost:" + port + "/users/1", User.class)
				.getFriendsIdSet()
				.contains(2L));
	}
}
