package com.example.notification_server.service;

import com.example.notification_server.exception.ConflictException;
import com.example.notification_server.exception.CustomAccessDeniedException;
import com.example.notification_server.mapper.SubscriptionMapper;
import com.example.notification_server.model.dto.ListSubscriptionsDTO;
import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.repository.SubscriptionRepository;
import com.example.notification_server.repository.TypeRepository;
import com.example.notification_server.util.JwtParserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.example.notification_server.util.ExceptionStringUtil.ACCESS_DENIED_EXCEPTION;
import static com.example.notification_server.util.ExceptionStringUtil.SUBSCRIPTION_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final TypeRepository typeRepository;
    private final SubscriptionMapper subscriptionMapper;
    private final JwtParserUtil jwtParserUtil;

    @Transactional
    public void addSubscription(String token, SubscriptionDTO request) {
        if (!jwtParserUtil.extractEmailFromJwt(token).equals(request.getEmail())) {
            throw new CustomAccessDeniedException(ACCESS_DENIED_EXCEPTION);
        }

        var typeEntity = typeRepository.findByName(request.getType())
                .orElseThrow(() -> new ConflictException("Тип объекта не поддерживается"));
        if (subscriptionRepository.existsByEmailAndElementIdAndTypeEntity(request.getEmail(), request.getElementId(), typeEntity)) {
            throw new ConflictException("Подписка на объект уже есть");
        }
        var subscriptionEntity = subscriptionMapper.toEntity(request);
        subscriptionEntity.setTypeEntity(typeEntity);
        subscriptionRepository.save(subscriptionEntity);
    }

    public ListSubscriptionsDTO getAllSubscriptions(String token, String email, int page, int size) {
        if (!jwtParserUtil.extractEmailFromJwt(token).equals(email)) {
            throw new CustomAccessDeniedException(ACCESS_DENIED_EXCEPTION);
        }

        Pageable pageable = PageRequest.of(page, size);
        var subscriptions = subscriptionRepository.findByEmail(email, pageable);
        return ListSubscriptionsDTO.builder()
                .subscriptions(subscriptions.getContent().stream().map(subscriptionMapper::toDto).toList())
                .currentPage(subscriptions.getNumber())
                .totalItems(subscriptions.getTotalElements())
                .totalPages(subscriptions.getTotalPages())
                .build();
    }

    @Transactional
    public void deleteAllSubscriptions(String token, String email) {
        if (!jwtParserUtil.extractEmailFromJwt(token).equals(email)) {
            throw new CustomAccessDeniedException(ACCESS_DENIED_EXCEPTION);
        }

        subscriptionRepository.deleteByEmail(email);
    }

    @Transactional
    public void deleteSubscriptionByElementId(String token, String email, UUID elementId) {
        if (!jwtParserUtil.extractEmailFromJwt(token).equals(email)) {
            throw new CustomAccessDeniedException(ACCESS_DENIED_EXCEPTION);
        }

        if (!subscriptionRepository.existsByEmailAndElementId(email, elementId)) {
            throw new IllegalArgumentException(SUBSCRIPTION_NOT_FOUND_EXCEPTION);
        }
        subscriptionRepository.deleteByEmailAndElementId(email, elementId);
    }

    @Transactional
    public void deleteSubscriptionBySubscriptionId(String token, String email, UUID subscriptionId) {
        if (!jwtParserUtil.extractEmailFromJwt(token).equals(email)) {
            throw new CustomAccessDeniedException(ACCESS_DENIED_EXCEPTION);
        }

        if (!subscriptionRepository.existsByIdAndEmail(subscriptionId, email)) {
            throw new IllegalArgumentException(SUBSCRIPTION_NOT_FOUND_EXCEPTION);
        }
        subscriptionRepository.deleteByIdAndEmail(subscriptionId, email);
    }

    public boolean isSubscriptionExisted(String token, UUID elementId) {
        String email = jwtParserUtil.extractEmailFromJwt(token);
        return subscriptionRepository.existsByEmailAndElementId(email, elementId);
    }
}