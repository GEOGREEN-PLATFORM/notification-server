package com.example.notification_server.service;

import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void send(String to, String subject, String text) throws MessagingException {
        mailSender.send(createMimeMessage(to, subject, text, null, null));
    }


    public void send(String to, String subject, String text, String fileName, DataSource file) throws MessagingException {
        mailSender.send(createMimeMessage(to, subject, text, fileName, file));
    }

    private MimeMessage createMimeMessage(
            String to, String subject, String text, String fileName, DataSource file) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setTo(to);
        helper.setText(text);
        if (file != null) {
            helper.addAttachment(fileName, file);
        }
        return message;
    }
}