package com.darko.taskservice.dto;

public record NotificationRequest(
        String eventType,
        Long taskId,
        String title,
        String message
) {
}