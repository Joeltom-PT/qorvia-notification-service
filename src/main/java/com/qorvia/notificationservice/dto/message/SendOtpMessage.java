package com.qorvia.notificationservice.dto.message;

import lombok.Data;

@Data
public class SendOtpMessage extends BaseMessage {
    private String email;
}
