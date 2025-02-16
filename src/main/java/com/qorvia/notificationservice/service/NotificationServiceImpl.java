package com.qorvia.notificationservice.service;

import com.qorvia.notificationservice.dto.BookingCompletedNotificationDTO;
import com.qorvia.notificationservice.dto.request.OrganizerStatusChangeMailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendStatusChangeEmail(OrganizerStatusChangeMailRequest mailRequest) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

            messageHelper.setTo(mailRequest.getEmail());
            messageHelper.setSubject("Organizer Status Change Notification");
            messageHelper.setText(mailRequest.getMessage(), true);

            mailSender.send(mimeMessage);
            log.info("Status change email sent to {}", mailRequest.getEmail());
        } catch (Exception e) {
            log.error("Failed to send status change email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendBookingCompletedNotification(BookingCompletedNotificationDTO notificationDTO) {
        try {
            notificationDTO.setTotalAmount(roundToTwoDecimalPlaces(notificationDTO.getTotalAmount()));
            notificationDTO.setTotalDiscount(roundToTwoDecimalPlaces(notificationDTO.getTotalDiscount()));

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

            messageHelper.setTo(notificationDTO.getEmail());
            messageHelper.setSubject("Booking Completed - " + notificationDTO.getEventName());

            String htmlContent = generateBookingCompletedEmailHtml(notificationDTO);

            messageHelper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Booking completed notification sent to {}", notificationDTO.getEmail());
        } catch (Exception e) {
            log.error("Failed to send booking completed notification email", e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private Double roundToTwoDecimalPlaces(Double value) {
        if (value == null) return null;
        return Double.valueOf(String.format("%.2f", value));
    }

    private String generateBookingCompletedEmailHtml(BookingCompletedNotificationDTO notificationDTO) {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                ".email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }" +
                ".email-header { background-color: #007bff; padding: 10px 20px; text-align: center; color: #ffffff; border-radius: 8px 8px 0 0; }" +
                ".email-content { padding: 20px; color: #333333; font-size: 16px; }" +
                ".email-footer { text-align: center; font-size: 14px; color: #777777; padding: 10px 0; border-top: 1px solid #eeeeee; }" +
                ".event-image { max-width: 100%; height: auto; border-radius: 8px; }" +
                ".cta-button { background-color: #28a745; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 4px; font-weight: bold; }" +
                ".cta-button:hover { background-color: #218838; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='email-container'>" +
                "<div class='email-header'>" +
                "<h2>Your Booking is Complete!</h2>" +
                "</div>" +
                "<div class='email-content'>" +
                "<p>Dear " + notificationDTO.getUserName() + ",</p>" +
                "<p>Thank you for booking with us! We're excited to confirm that your booking for the event <strong>" +
                notificationDTO.getEventName() + "</strong> has been successfully completed.</p>" +
                "<img src='" + notificationDTO.getImageUrl() + "' alt='Event Image' class='event-image' />" +
                "<p>Total Amount: <strong>$" + notificationDTO.getTotalAmount() + "</strong></p>" +
                "<p>Total Discount: <strong>$" + notificationDTO.getTotalDiscount() + "</strong></p>" +
                "<p>Status: <strong>" + notificationDTO.getPaymentStatus() + "</strong></p>" +
                "<p>We're looking forward to seeing you at the event!</p>" +
                "<p>Best regards,<br>Qorvia Team</p>" +
                "</div>" +
                "<div class='email-footer'>" +
                "<p>If you have any questions, feel free to contact us.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}
