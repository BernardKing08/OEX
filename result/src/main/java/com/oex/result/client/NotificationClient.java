package com.oex.result.client;

import com.oex.result.dto.request.ResultNotificationRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

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

    @CircuitBreaker(name = "notificationCircuitBreaker", fallbackMethod = "logFailure")
    @Retry(name = "notificationRetry", fallbackMethod = "logFailure")
    public void sendResultNotification(ResultNotificationRequest request) {
        sendRequest(request);
    }

    private Void sendRequest(ResultNotificationRequest request) {
        webClient.post()
                .uri("/api/notifications/result")
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .block();
        return null;
    }

    private void logFailure(ResultNotificationRequest request, Throwable throwable) {
        logger.warn("Failed to notify student {} of their result: {}",
                request.getStudentId(), throwable.getMessage());
    }


}
