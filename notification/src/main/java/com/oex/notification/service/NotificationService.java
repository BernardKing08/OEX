package com.oex.notification.service;

import com.oex.notification.dto.request.ResultNotificationRequest;

public interface NotificationService {

    void sendResultNotification(ResultNotificationRequest request);

}
