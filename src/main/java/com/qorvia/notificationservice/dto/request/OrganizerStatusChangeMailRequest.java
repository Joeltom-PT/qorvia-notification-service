package com.qorvia.notificationservice.dto.request;

import lombok.Data;

@Data
public class OrganizerStatusChangeMailRequest {
    private String email;
    private String message;
    private String registerRequestStatus;
}
