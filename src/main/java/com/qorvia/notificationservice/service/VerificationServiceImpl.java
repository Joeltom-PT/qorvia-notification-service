package com.qorvia.notificationservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.qorvia.notificationservice.dto.request.ResendOtpRequest;
import com.qorvia.notificationservice.dto.response.ApiResponse;
import com.qorvia.notificationservice.dto.response.OrganizerVerificationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
public class VerificationServiceImpl implements VerificationService {

    private final Cache<String, String> otpCache;
    private final JavaMailSender mailSender;

    @Value("${baseUrlOfTheSite}")
    private String baseUrlOfTheSite;
    public VerificationServiceImpl(Cache<String, String> otpCache, JavaMailSender mailSender) {
        this.otpCache = otpCache;
        this.mailSender = mailSender;
    }

    @Override
    public void setOtp(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null");
        }
        String otp = generateOtp();
        otpCache.put(email, otp);
        sendEmail(email, otp);
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        if (email == null || otp == null) {
            throw new IllegalArgumentException("Email and OTP must not be null");
        }
        String storedOtp = otpCache.getIfPresent(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpCache.invalidate(email);
            return true;
        }
        return false;
    }

    @Override
    public void resendOtp(ResendOtpRequest request) {
        if (request.getEmail() == null) {
            throw new IllegalArgumentException("Email must not be null");
        }
        String newOtp = generateOtp();
        otpCache.put(request.getEmail(), newOtp);
        sendEmail(request.getEmail(), newOtp);
    }

    @Override
    public String organizerEmailVerification(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email must not be null");
        }

        String token = generateToken(email);
        otpCache.put(token, email);

        String verificationLink = baseUrlOfTheSite + "/verifyOrganizer/" + token;
        sendVerificationEmail(email, verificationLink);

        return verificationLink;
    }

    @Override
    public OrganizerVerificationResponse organizerEmailVerify(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }

        log.info("Received token for verification: {}", token);

        String decodedToken;
        try {
            decodedToken = decodeToken(token);
            log.info("Decoded token: {}", decodedToken);
        } catch (RuntimeException e) {
            log.error("Failed to decode token: {}", token, e);
            // Return null or an empty response when decoding fails
            return null;
        }

        String[] parts = decodedToken.split(":");
        if (parts.length != 2) {
            log.error("Invalid token format. Decoded token: {}", decodedToken);
            return null;
        }

        String storedEmail = otpCache.getIfPresent(token);
        String providedEmail = parts[1];

        log.info("Stored email from cache: {}", storedEmail);
        log.info("Provided email from token: {}", providedEmail);

        if (storedEmail != null && storedEmail.equals(providedEmail)) {
            otpCache.invalidate(token);
            log.info("Email verification successful for email: {}", storedEmail);

            OrganizerVerificationResponse response = new OrganizerVerificationResponse();
            response.setEmail(storedEmail);

            return response;
        }

        log.error("Invalid token or email. Token: {}, Stored Email: {}, Provided Email: {}", token, storedEmail, providedEmail);
        return null;
    }



    private String generateOtp() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    private String generateToken(String email) {
        String uuid = UUID.randomUUID().toString();
        String token = uuid + ":" + email;
        return Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    private String decodeToken(String token) {
        try {
            String urlDecodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8.toString());
            byte[] decodedBytes = Base64.getDecoder().decode(urlDecodedToken);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to decode token", e);
        }
    }

    private void sendEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP code is: " + otp);
        mailSender.send(message);
    }

    private void sendVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("Please verify your email by clicking the link: " + verificationLink);
        mailSender.send(message);
    }
}
