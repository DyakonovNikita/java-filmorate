package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    private final UserRowMapper mapper;

    public List<User> findAll() {
        String query = "select * from app_users";
        return jdbcTemplate.query(query, mapper);
    }

    public User find(Long userId) {
        String query = "select u.* from app_users AS u JOIN PUBLIC.FRIENDS F on u.ID = F.RECIPIENT_ID where id = ?";
        System.out.print(jdbcTemplate.query(query, mapper, userId));
        return jdbcTemplate.query(query, mapper, userId).getFirst();
    }

    public User create(User user) {
        String query = "insert into app_users (name, email, login) values (?, ?, ?)";
        InMemoryUserStorage.validateUser(user);
        return jdbcTemplate.query(query, mapper, user).getFirst();
    }

    public User update(User user) {
        return user;
    }
}
