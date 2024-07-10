package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Duration duration;
    private Set<Long> likedUserIdSet;

    public Film(Long id, String name, String description, LocalDate releaseDate, Duration duration, Set<Long> likedUserIdSet) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likedUserIdSet = Objects.requireNonNullElseGet(likedUserIdSet, HashSet::new);
    }
}