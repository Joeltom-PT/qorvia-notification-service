package com.qorvia.notificationservice.dto.message;

import lombok.Data;

@Data
public class OrganizerEmailVerifyMessage extends BaseMessage {
    private String token;
}
