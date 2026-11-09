package com.oex.notification.client;

import com.oex.notification.dto.UserInfo;
import com.oex.notification.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class AuthenticationClient {

    private final WebClient webClient;

    public AuthenticationClient(WebClient.Builder webClientBuilder,
                                 @Value("${oex.services.authentication.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public UserInfo getUser(Long userId) {
        try {
            return webClient.get()
                    .uri("/api/users/{id}", userId)
                    .retrieve()
                    .bodyToMono(UserInfo.class)
                    .block();
        } catch (WebClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }
            throw exception;
        }
    }

}
