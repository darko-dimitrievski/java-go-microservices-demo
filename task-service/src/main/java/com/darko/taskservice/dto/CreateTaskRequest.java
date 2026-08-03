package com.darko.taskservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskRequest(
        @NotBlank(message = "title is required") String title,
        String description
) {
}