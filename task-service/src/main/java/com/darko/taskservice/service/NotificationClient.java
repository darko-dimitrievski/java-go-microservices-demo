package com.darko.taskservice.service;

import com.darko.taskservice.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestClient restClient;

    public NotificationClient(RestClient notificationRestClient) {
        this.restClient = notificationRestClient;
    }

    /**
     * Fire-and-forget style call to the Go notification-service.
     * Failures are logged but never break the task workflow -
     * notification is a side effect, not a source of truth.
     */
    public void notify(NotificationRequest request) {
        try {
            restClient.post()
                    .uri("/webhook/notify")
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn("Failed to notify downstream service for taskId={}: {}",
                    request.taskId(), ex.getMessage());
        }
    }
}