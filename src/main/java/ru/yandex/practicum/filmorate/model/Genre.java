package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class Genre {
    @NonNull
    private final Long id;
    @NotBlank(message = "Ошибка! Название не может быть пустым.")
    private final String name;
}