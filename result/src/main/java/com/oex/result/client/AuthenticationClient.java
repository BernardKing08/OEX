package com.oex.result.client;

import com.oex.result.dto.UserInfo;
import com.oex.result.exception.ResourceNotFoundException;
import com.oex.result.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
public class AuthenticationClient {

    private final WebClient webClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public AuthenticationClient(WebClient.Builder webClientBuilder,
                                 @Value("${oex.services.authentication.base-url}") String baseUrl,
                                 CircuitBreakerFactory circuitBreakerFactory) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public UserInfo getUser(Long userId) {
        UserInfo userInfo = circuitBreakerFactory.create("authenticationCircuitBreaker")
                .run(() -> fetchUser(userId), throwable -> fallbackUser(throwable));
        if (userInfo == null) {
            throw new ResourceNotFoundException("Student not found with id: " + userId);
        }
        return userInfo;
    }

    private UserInfo fetchUser(Long userId) {
        return webClient.get()
                .uri("/api/users/{id}", userId)
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }
                    if (response.statusCode().isError()) {
                        return response.createException().flatMap(Mono::error);
                    }
                    return response.bodyToMono(UserInfo.class);
                })
                .block();
    }

    private UserInfo fallbackUser(Throwable throwable) {
        throw new ServiceUnavailableException("Authentication service is currently unavailable");
    }

}
