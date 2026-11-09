package com.oex.result.client;

import com.oex.result.dto.request.ResultNotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationClient.class);

    private final WebClient webClient;

    public NotificationClient(WebClient.Builder webClientBuilder,
                               @Value("${oex.services.notification.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public void sendResultNotification(ResultNotificationRequest request) {
        try {
            webClient.post()
                    .uri("/api/notifications/result")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception exception) {
            // Notification is best-effort: a saved result should not be lost just because
            // the notification service is slow or temporarily down.
            logger.warn("Failed to notify student {} of their result: {}",
                    request.getStudentId(), exception.getMessage());
        }
    }

}
