package com.qorvia.notificationservice.dto.message;

import lombok.Data;

@Data
public class OrganizerStatusChangeMailRequestMessage extends BaseMessage {
    private String email;
    private String message;
    private String registerRequestStatus;
}
