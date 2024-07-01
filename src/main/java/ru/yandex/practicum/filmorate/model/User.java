package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friendsIdSet;

    public User(Long id, String email, String login, String name, LocalDate birthday, Set<Long> friendsIdSet) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friendsIdSet = Objects.requireNonNullElseGet(friendsIdSet, HashSet::new);
    }
}
