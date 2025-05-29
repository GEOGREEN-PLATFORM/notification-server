package com.example.notification_server.service;


import com.example.notification_server.model.dto.UpdateElementDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private SubscriberNotificationService subscriberNotificationService;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    private UpdateElementDTO sampleDto;

    @BeforeEach
    void setUp() {
        sampleDto = new UpdateElementDTO(
                UUID.randomUUID(),
                "ELEMENT_TYPE",
                "ACTIVE",
                OffsetDateTime.now()
        );
    }

    @Test
    void whenListenUpdateElement_thenNotifyUsersCalled() {
        kafkaConsumer.listenUpdateElement(sampleDto);

        ArgumentCaptor<UpdateElementDTO> captor = ArgumentCaptor.forClass(UpdateElementDTO.class);
        verify(subscriberNotificationService).notifyUsers(captor.capture());

        UpdateElementDTO captured = captor.getValue();
        assertEquals(sampleDto.getElementId(), captured.getElementId(), "Element ID should match");
        assertEquals(sampleDto.getType(), captured.getType(), "Type should match");
        assertEquals(sampleDto.getStatus(), captured.getStatus(), "Status should match");
        assertEquals(sampleDto.getDate(), captured.getDate(), "Date should match");
    }
}

