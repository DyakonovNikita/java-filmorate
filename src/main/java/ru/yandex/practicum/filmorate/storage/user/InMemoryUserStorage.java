package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public List<User> findAll() {
        log.trace("findAll is called");
        return (List<User>) users.values();
    }

    public User find(Long userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }
        throw new NotFoundException("Пользователь id: " + userId + " не найден");
    }

    public Optional<User> create(User user) {
        log.trace("create is called");
        validateUser(user);
        log.debug("new user passed validation");

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("new user with id = {} add into users map", user.getId());
        return Optional.of(user);
    }

    static void validateUser(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.warn("Некорректный email = {}", user.getEmail());
            throw new ValidationException("Некорректный e-mail");
        }
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.warn("Некорректный login = {}", user.getLogin());
            throw new ValidationException("Некорректный логин");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Некорректный birthday = {}", user.getBirthday());
            throw new ValidationException("Некоректная дата рождения");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.debug("Пустое поле name заменено на login = {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private Long getNextId() {
        log.trace("getNextId is called.");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("generated next id = {}", currentMaxId + 1);
        return ++currentMaxId;
    }

    public Optional<User> update(User newUser) {
        log.trace("update is called");
        if (newUser.getId() == null) {
            log.warn("id is null");
            throw new NullPointerException("id не может равняться null");
        }
        if (users.containsKey(newUser.getId())) {
            log.trace("film with id = {} found", newUser.getId());
            validateUser(newUser);
            log.trace("new film passed validation");
            User oldUser = users.get(newUser.getId());
            oldUser.setName(newUser.getName());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setBirthday(newUser.getBirthday());
            return Optional.of(oldUser);
        }
        log.warn("user with id = {} not found", newUser.getId());
        throw new NotFoundException("Пользователь id: " + newUser.getId() + " не найден");
    }

    @Override
    public boolean delete(User user) {
        return users.remove(user.getId()).equals(user);
    }

    @Override
    public List<User> findUsers() {
        return List.of();
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.empty();
    }
}
