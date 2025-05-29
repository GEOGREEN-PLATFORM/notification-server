package com.example.notification_server.service;

import com.example.notification_server.model.dto.UpdateElementDTO;
import com.example.notification_server.model.entity.SubscriptionEntity;
import com.example.notification_server.repository.SubscriptionRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriberNotificationService {
    private final MailSenderService mailSenderService;
    private final SubscriptionRepository subscriptionRepository;
    private final TextGenerationService textGenerationService;

    public void notifyUsers(UpdateElementDTO updateElementDTO) {
        int page = 0;
        int pageSize = 5;

        Page<SubscriptionEntity> result;

        do {
            result = subscriptionRepository.findByElementId(updateElementDTO.getElementId(), PageRequest.of(page, pageSize));
            var subscriptions = result.getContent();

            if (!subscriptions.isEmpty()) {
                try {
                    mailSenderService.send(
                            subscriptions.stream().map(SubscriptionEntity::getEmail).toArray(String[]::new),
                            textGenerationService.generateTitle(updateElementDTO),
                            textGenerationService.generateText(updateElementDTO)
                    );
                } catch (MessagingException e) {
                    log.error("{}:{}", e.getClass(), e.getMessage());
                }
            }

            page++;
        } while (result.hasNext());
    }
}