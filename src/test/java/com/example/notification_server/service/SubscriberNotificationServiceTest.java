package com.example.notification_server.service;


import com.example.notification_server.model.dto.UpdateElementDTO;
import com.example.notification_server.model.entity.SubscriptionEntity;
import com.example.notification_server.repository.SubscriptionRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriberNotificationServiceTest {

    @Mock
    private MailSenderService mailSenderService;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private TextGenerationService textGenerationService;

    @InjectMocks
    private SubscriberNotificationService notificationService;

    private UpdateElementDTO dto;
    private UUID elementId;

    @BeforeEach
    void setUp() {
        elementId = UUID.randomUUID();
        dto = new UpdateElementDTO(elementId, "TYPE", "STATUS", OffsetDateTime.now());
    }

    @Test
    void whenNoSubscriptions_thenNoSendInvoked() throws MessagingException {
        Page<SubscriptionEntity> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 5), 1);
        when(subscriptionRepository.findByElementId(elementId, PageRequest.of(0, 5))).thenReturn(emptyPage);

        notificationService.notifyUsers(dto);

        verify(mailSenderService, never()).send(any(), anyString(), anyString());
    }
}
