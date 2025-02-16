package com.qorvia.notificationservice.dto.message;

import lombok.Data;

@Data
public class OrganizerEmailVerificationRequestMessage extends BaseMessage {
    private String email;
}
