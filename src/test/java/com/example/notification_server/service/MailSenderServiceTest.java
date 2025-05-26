package com.example.notification_server.service;


import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MailSenderService mailSenderService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        ReflectionTestUtils.setField(mailSenderService, "from", "test@domain.com");
    }

    @Test
    void whenSend_thenMailSenderSendWithProperMessage() throws MessagingException, Exception {
        String[] to = {"user@example.com"};
        String subject = "Test Subject";
        String text = "Hello, world!";

        mailSenderService.send(to, subject, text);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());

        MimeMessage sent = captor.getValue();
        assertEquals(subject, sent.getSubject(), "Subject should match");
        assertNotNull(sent.getFrom(), "From should not be null");
        assertEquals(1, sent.getFrom().length, "Should have one 'from' address");
        assertEquals("test@domain.com", sent.getFrom()[0].toString(), "From address should match injected value");
        assertNotNull(sent.getAllRecipients(), "Recipients should not be null");
        assertEquals(1, sent.getAllRecipients().length, "Should have one recipient");
        assertEquals("user@example.com", sent.getAllRecipients()[0].toString(), "Recipient address should match");

        Object content = sent.getContent();
        String bodyText = extractText(content);
        assertEquals(text, bodyText, "Body text should match");
    }

    private String extractText(Object content) throws Exception {
        if (content instanceof String) {
            return (String) content;
        }
        if (content instanceof MimeMultipart) {
            MimeMultipart multipart = (MimeMultipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                Object partContent = part.getContent();
                String text = extractText(partContent);
                if (text != null) {
                    return text;
                }
            }
        }
        return null;
    }
}
