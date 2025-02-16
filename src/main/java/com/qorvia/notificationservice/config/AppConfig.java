package com.qorvia.notificationservice.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Properties;

@Configuration
public class AppConfig {

    @Bean
    public com.github.benmanes.caffeine.cache.Cache<String, String> otpCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .build();
    }
}
