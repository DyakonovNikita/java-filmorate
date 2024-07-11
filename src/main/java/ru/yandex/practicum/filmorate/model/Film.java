package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private Long id;
    @NonNull
    @NotBlank(message = "Ошибка! Название не может быть пустым.")
    private String name;
    @NonNull
    private String description;
    @NonNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @Positive(message = "Ошибка! Продолжительность фильма должна быть положительной.")
    private int duration;
    private int rate;
    private Set<Long> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();
    private RatingMPA mpa;
}