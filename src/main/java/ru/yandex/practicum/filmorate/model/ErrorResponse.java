package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String error;
    private String description = "";

    public ErrorResponse(String error) {
        this.error = error;
        this.description = description;
    }
}
