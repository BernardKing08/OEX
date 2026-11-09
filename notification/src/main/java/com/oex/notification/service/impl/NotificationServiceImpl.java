package com.oex.notification.service.impl;

import com.oex.notification.client.AuthenticationClient;
import com.oex.notification.dto.UserInfo;
import com.oex.notification.dto.request.ResultNotificationRequest;
import com.oex.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final AuthenticationClient authenticationClient;

    @Override
    public void sendResultNotification(ResultNotificationRequest request) {
        UserInfo student = authenticationClient.getUser(request.getStudentId());

        String status = Boolean.TRUE.equals(request.getPassed()) ? "PASSED" : "FAILED";
        String message = String.format(
                "Hi %s, your result for \"%s\" is in: %d/%d - %s",
                student.getFirstName(), request.getExamTitle(), request.getScore(),
                request.getTotalMarks(), status);

        // No SMTP server is configured yet, so the send is simulated via logging for now.
        logger.info("Notifying {} <{}>: {}", student.getFirstName() + " " + student.getLastName(),
                student.getEmail(), message);
    }

}
