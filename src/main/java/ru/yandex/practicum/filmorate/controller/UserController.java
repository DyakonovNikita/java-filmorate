package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @Validated
    public User create(@Valid @RequestBody User user) {
        user = ValidatorControllers.validateUser(user);
        User newUser = userService.create(user);
        return newUser;
    }

    @PutMapping
    @Validated
    public User update(@Valid @RequestBody User user) {
        user = ValidatorControllers.validateUser(user);
        User newUser = userService.update(user);
        return newUser;
    }

    @DeleteMapping
    @Validated
    public void delete(@Valid @RequestBody User user) {
        userService.delete(user);
    }

    @GetMapping
    public List<User> findUsers() {
        List<User> users = userService.findUsers();
        return users;
    }

    @GetMapping("/{userId}")
    public User findUserById(@PathVariable long userId) {
        User user = userService.findUserById(userId);
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public boolean addInFriends(@PathVariable long id, @PathVariable long friendId) {
        if (userService.addInFriends(id, friendId)) {
            return true;
        }
        return false;
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public boolean deleteFromFriends(@PathVariable long id, @PathVariable long friendId) {
        if (userService.deleteFromFriends(id, friendId)) {
            return true;
        }
        return false;
    }

    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable long id) {
        List<User> users = userService.findFriends(id);
        return users;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        List<User> users = userService.findMutualFriends(id, otherId);
        return users;
    }

}
