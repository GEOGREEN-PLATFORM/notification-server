package com.example.notification_server.service;

import com.example.notification_server.model.dto.UpdateElementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    private final SubscriberNotificationService subscriberNotificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.update-element}",
            groupId = "${spring.kafka.group-id.update-element}",
            containerFactory = "factory"
    )
    public void listenFinishRegistration(@Payload UpdateElementDTO updateElementDTO) {
        log.info("Get message {}", updateElementDTO);
        subscriberNotificationService.notifyUsers(updateElementDTO);
    }
}