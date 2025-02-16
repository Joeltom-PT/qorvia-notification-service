package com.qorvia.notificationservice.dto.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SendOtpMessage.class, name = "send-otp"),
        @JsonSubTypes.Type(value = VerifyOtpMessage.class, name = "verify-otp"),
        @JsonSubTypes.Type(value = OrganizerEmailVerificationRequestMessage.class, name = "verify-email"),
        @JsonSubTypes.Type(value = OrganizerEmailVerifyMessage.class, name = "verify-token"),
        @JsonSubTypes.Type(value = OrganizerStatusChangeMailRequestMessage.class, name = "status-change")
})
public class BaseMessage {
    private String type;
}
