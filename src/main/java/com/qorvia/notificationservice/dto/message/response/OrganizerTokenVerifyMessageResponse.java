package com.qorvia.notificationservice.dto.message.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerTokenVerifyMessageResponse {
    private String email;
    private String message;
}
