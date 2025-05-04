package com.example.notification_server.service;

import com.example.notification_server.exception.ConflictException;
import com.example.notification_server.mapper.SubscriptionMapper;
import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.repository.SubscriptionRepository;
import com.example.notification_server.repository.TypeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final TypeRepository typeRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional
    public void addSubscription(SubscriptionDTO request) {
        var typeEntity = typeRepository.findByName(request.getType())
                .orElseThrow(() -> new ConflictException("Тип объекта не поддерживается"));
        if (subscriptionRepository.existsByEmailAndElementIdAndTypeEntity(request.getEmail(), request.getElementId(), typeEntity)) {
            throw new ConflictException("Подписка на объект уже есть");
        }
        var subscriptionEntity = subscriptionMapper.toEntity(request);
        subscriptionEntity.setTypeEntity(typeEntity);
        subscriptionRepository.save(subscriptionEntity);
    }

    @Transactional
    public void deleteSubscription(SubscriptionDTO request) {
        var typeEntity = typeRepository.findByName(request.getType())
                .orElseThrow(() -> new ConflictException("Тип объекта не поддерживается"));
        subscriptionRepository.deleteByEmailAndElementIdAndTypeEntity(request.getEmail(), request.getElementId(), typeEntity);
    }
}