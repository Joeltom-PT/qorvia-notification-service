package com.qorvia.notificationservice.service;

import com.qorvia.notificationservice.dto.BookingCompletedNotificationDTO;
import com.qorvia.notificationservice.dto.request.OrganizerStatusChangeMailRequest;

public interface NotificationService {
    void sendStatusChangeEmail(OrganizerStatusChangeMailRequest mailRequest);

    void sendBookingCompletedNotification(BookingCompletedNotificationDTO notificationDTO);
}
