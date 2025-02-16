package com.qorvia.notificationservice.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qorvia.notificationservice.dto.BookingCompletedNotificationDTO;
import com.qorvia.notificationservice.dto.message.*;
import com.qorvia.notificationservice.dto.message.response.OrganizerTokenVerifyMessageResponse;
import com.qorvia.notificationservice.dto.message.response.VerifyOtpMessageResponse;
import com.qorvia.notificationservice.dto.request.OrganizerStatusChangeMailRequest;
import com.qorvia.notificationservice.dto.response.OrganizerVerificationResponse;
import com.qorvia.notificationservice.service.NotificationService;
import com.qorvia.notificationservice.service.VerificationService;
import com.qorvia.notificationservice.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class RabbitMQReceiver {

    private final ObjectMapper objectMapper;
    private final VerificationService verificationService;
    private final NotificationService notificationService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {AppConstants.NOTIFICATION_SERVICE_ASYNC_QUEUE, AppConstants.NOTIFICATION_SERVICE_RPC_QUEUE})
    public void receiveMessage(Message amqpMessage) {
        try {
            byte[] messageBytes = amqpMessage.getBody();
            log.info("I am getting the message bytes as : ========================================== : {}", new String(messageBytes, StandardCharsets.UTF_8));
            MessageProperties amqpProps = amqpMessage.getMessageProperties();
            String correlationId = amqpProps.getCorrelationId();
            if (correlationId != null) {
                log.info("Received RPC message with correlation ID: {}", correlationId);
            }

            Map<String, Object> messageMap = objectMapper.readValue(messageBytes, Map.class);
            String type = (String) messageMap.get("type");

            switch (type) {
                case "send-otp":
                    SendOtpMessage sendOtpMessage = objectMapper.convertValue(messageMap, SendOtpMessage.class);
                    handleSendOtpMessage(sendOtpMessage);
                    break;
                case "verify-otp":
                    VerifyOtpMessage verifyOtpMessage = objectMapper.convertValue(messageMap, VerifyOtpMessage.class);
                    VerifyOtpMessageResponse response = handleVerifyOtpMessage(verifyOtpMessage);
                    sendRpcResponse(amqpProps, response);
                    break;
                case "verify-email":
                    OrganizerEmailVerificationRequestMessage verifyEmailMessage = objectMapper.convertValue(messageMap, OrganizerEmailVerificationRequestMessage.class);
                    handleOrganizerEmailVerificationRequest(verifyEmailMessage);
                    break;
                case "verify-token":
                    OrganizerEmailVerifyMessage verifyTokenMessage = objectMapper.convertValue(messageMap, OrganizerEmailVerifyMessage.class);
                    OrganizerTokenVerifyMessageResponse tokenResponse = handleOrganizerTokenVerifyMessage(verifyTokenMessage);
                    sendRpcResponse(amqpProps, tokenResponse);
                    break;
                case "status-change":
                    OrganizerStatusChangeMailRequestMessage statusChangeMessage = objectMapper.convertValue(messageMap, OrganizerStatusChangeMailRequestMessage.class);
                    handleOrganizerStatusChangeMailRequest(statusChangeMessage);
                    break;
                case "booking-completed-notification":
                    BookingCompletedNotificationMessage message = objectMapper.convertValue(messageMap, BookingCompletedNotificationMessage.class);
                    handleBookingCompletedNotificationMessage(message);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown message type: " + type);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize message", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to process message", e);
        }
    }

    private void handleSendOtpMessage(SendOtpMessage message) {
        verificationService.setOtp(message.getEmail());
        log.info("Sending OTP to: {}", message.getEmail());
    }

    private VerifyOtpMessageResponse handleVerifyOtpMessage(VerifyOtpMessage message) {
        log.info("Verifying OTP for: {}", message.getEmail());
        boolean isVerified = verificationService.verifyOtp(message.getEmail(), message.getOtp());
        return new VerifyOtpMessageResponse(isVerified, "OTP verified successfully");
    }

    private void handleOrganizerEmailVerificationRequest(OrganizerEmailVerificationRequestMessage message) {
        log.info("Verifying organizer email: {}", message.getEmail());
        verificationService.organizerEmailVerification(message.getEmail());
    }

    private OrganizerTokenVerifyMessageResponse handleOrganizerTokenVerifyMessage(OrganizerEmailVerifyMessage message) {
        log.info("Verifying organizer token: {}", message.getToken());
        try {
            OrganizerVerificationResponse verificationResponse = verificationService.organizerEmailVerify(message.getToken());

            if (verificationResponse.getEmail() == null) {
                log.warn("Email is null for token: {}", message.getToken());
                return new OrganizerTokenVerifyMessageResponse(null, "Email is null");
            }

            return new OrganizerTokenVerifyMessageResponse(verificationResponse.getEmail(), "Token verified successfully");
        } catch (Exception e) {
            log.error("Error verifying organizer token: {}", message.getToken(), e);
            return new OrganizerTokenVerifyMessageResponse(null, "Error verifying token");
        }
    }

    private void handleOrganizerStatusChangeMailRequest(OrganizerStatusChangeMailRequestMessage message) {
        log.info("Sending status change email to: {}", message.getEmail());
        log.info("Message: {}", message.getMessage());
        log.info("Status: {}", message.getRegisterRequestStatus());

        OrganizerStatusChangeMailRequest statusChangeMailRequest = new OrganizerStatusChangeMailRequest();
        statusChangeMailRequest.setEmail(message.getEmail());
        statusChangeMailRequest.setRegisterRequestStatus(message.getRegisterRequestStatus());
        statusChangeMailRequest.setMessage(message.getMessage());

        notificationService.sendStatusChangeEmail(statusChangeMailRequest);
    }

    public void handleBookingCompletedNotificationMessage(BookingCompletedNotificationMessage message){

        BookingCompletedNotificationDTO bookingCompletedNotificationDTO = new BookingCompletedNotificationDTO();
        bookingCompletedNotificationDTO.setEmail(message.getEmail());
        bookingCompletedNotificationDTO.setEventName(message.getEventName());
        bookingCompletedNotificationDTO.setImageUrl(message.getImageUrl());
        bookingCompletedNotificationDTO.setTotalAmount(message.getTotalAmount());
        bookingCompletedNotificationDTO.setTotalDiscount(message.getTotalDiscount());
        bookingCompletedNotificationDTO.setPaymentStatus(message.getPaymentStatus());
        bookingCompletedNotificationDTO.setUserName(message.getUserName());

        notificationService.sendBookingCompletedNotification(bookingCompletedNotificationDTO);

    }

    private void sendRpcResponse(MessageProperties amqpProps, Object response) throws JsonProcessingException {
        byte[] responseBytes = objectMapper.writeValueAsBytes(response);
        MessageProperties responseProperties = new MessageProperties();
        responseProperties.setCorrelationId(amqpProps.getCorrelationId());
        responseProperties.setContentType("application/octet-stream");

        Message responseMessage = new Message(responseBytes, responseProperties);
        rabbitTemplate.send(amqpProps.getReplyTo(), responseMessage);
    }
}
