package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class RatingMPA {
    @NonNull
    private final Long id;
    @NotBlank(message = "Ошибка! Название не может быть пустым.")
    private final String name;
    @NotBlank(message = "Ошибка! Описание не может быть пустым.")
    private final String description;

}
