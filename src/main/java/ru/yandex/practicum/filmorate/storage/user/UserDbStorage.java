package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public User create(User user) {
        String sqlQuery = "insert into app_users(name, email, login, birthday) values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    public User update(User user) {
        String sqlQuery = "update users set name = ?, email = ?, login = ?, birthday = ?" +
                " where id = ?";
        jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(),
                user.getBirthday(), user.getId());
        return user;
    }

    public boolean delete(User user) {
        if (findUserById(user.getId()) == null) {
            return false;
        }
        String sqlQuery = "delete from app_users where id = ?";
        return jdbcTemplate.update(sqlQuery, user.getId()) > 0;
    }

    public List<User> findAll() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::makeUser);
    }

    public User find(Long userId) {
        String sqlQuery = "select * from users where user_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::makeUser, userId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User(rs.getString("email"), rs.getString("login"),
                rs.getDate("birthday").toLocalDate());
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("user_name"));
        return user;
    }

}
