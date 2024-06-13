package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.trace("findAll is called");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.trace("create is called");
        validateUser(user);
        log.debug("new user passed validation");

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("new user with id = {} add into users map", user.getId());
        return user;
    }

    private void validateUser(User user) {
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
        log.trace("getNextId is called");
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        log.debug("generated next id = {}", currentMaxId + 1);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
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
            return oldUser;
        }
        log.warn("user with id = {} not found", newUser.getId());
        throw new NotFoundException("Пользователь id: " + newUser.getId() + " не найден");
    }
}
