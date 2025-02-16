package com.qorvia.notificationservice.dto.message.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpMessageResponse {
    private Boolean isVerified;
    private String message;
}
