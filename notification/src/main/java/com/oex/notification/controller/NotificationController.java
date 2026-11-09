package com.oex.notification.controller;

import com.oex.notification.dto.request.ResultNotificationRequest;
import com.oex.notification.dto.response.MessageResponse;
import com.oex.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/result")
    public ResponseEntity<MessageResponse> sendResultNotification(
            @Valid @RequestBody ResultNotificationRequest request) {

        notificationService.sendResultNotification(request);

        return ResponseEntity.ok(MessageResponse.builder()
                .message("Result notification sent.")
                .build());
    }

}
