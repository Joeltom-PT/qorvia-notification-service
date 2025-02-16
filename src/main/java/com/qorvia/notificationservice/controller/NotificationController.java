package com.qorvia.notificationservice.controller;

import com.qorvia.notificationservice.dto.BookingCompletedNotificationDTO;
import com.qorvia.notificationservice.dto.request.OrganizerStatusChangeMailRequest;
import com.qorvia.notificationservice.dto.response.ApiResponse;
import com.qorvia.notificationservice.service.NotificationService;
import com.qorvia.notificationservice.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/changeStatusMail")
    public ResponseEntity<ApiResponse<String>> organizerStatusChangeMail(@RequestBody OrganizerStatusChangeMailRequest mailRequest) {
        try {
            notificationService.sendStatusChangeEmail(mailRequest);
            return ResponseUtil.buildResponse(HttpStatus.OK, "Email sent successfully", null);
        } catch (Exception e) {
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send email: " + e.getMessage(), null);
        }
    }


    @PostMapping("/booking-completed")
    public ResponseEntity<ApiResponse<String>> sendBookingCompletedNotification(@RequestBody BookingCompletedNotificationDTO notificationDTO) {
        log.info("Received notification request for booking completed!");
        try {
            notificationService.sendBookingCompletedNotification(notificationDTO);
            return ResponseUtil.buildResponse(HttpStatus.OK, "Booking completed notification sent successfully", null);
        } catch (Exception e) {
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send booking completed notification: " + e.getMessage(), null);
        }
    }



}
