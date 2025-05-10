package com.example.notification_server.service;

import com.example.notification_server.model.dto.UpdateElementDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

public class KafkaConsumer {
    @KafkaListener(
            topics = "${spring.kafka.topic.update-element}",
            groupId = "${spring.kafka.group-id.update-element}",
            containerFactory = "factory"
    )
    public void listenFinishRegistration(@Payload UpdateElementDTO updateElementDTO) {

    }
}