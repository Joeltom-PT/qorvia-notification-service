package com.qorvia.notificationservice.dto.message;

import lombok.Data;

@Data
public class VerifyOtpMessage extends BaseMessage {
    private String email;
    private String otp;
}
