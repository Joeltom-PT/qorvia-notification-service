package com.qorvia.notificationservice.service;

import com.qorvia.notificationservice.dto.request.ResendOtpRequest;
import com.qorvia.notificationservice.dto.response.ApiResponse;
import com.qorvia.notificationservice.dto.response.OrganizerVerificationResponse;
import org.springframework.http.ResponseEntity;

public interface VerificationService {
    void setOtp(String email);
    boolean verifyOtp(String email, String otp);

    void resendOtp(ResendOtpRequest request);

    String organizerEmailVerification(String email);


    OrganizerVerificationResponse organizerEmailVerify(String token);
}