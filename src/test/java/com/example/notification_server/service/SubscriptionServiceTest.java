package com.example.notification_server.service;


import com.example.notification_server.exception.ConflictException;
import com.example.notification_server.exception.CustomAccessDeniedException;
import com.example.notification_server.mapper.SubscriptionMapper;
import com.example.notification_server.model.dto.ListSubscriptionsDTO;
import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.model.entity.SubscriptionEntity;
import com.example.notification_server.model.entity.TypeEntity;
import com.example.notification_server.repository.SubscriptionRepository;
import com.example.notification_server.repository.TypeRepository;
import com.example.notification_server.util.JwtParserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.notification_server.util.ExceptionStringUtil.ACCESS_DENIED_EXCEPTION;
import static com.example.notification_server.util.ExceptionStringUtil.SUBSCRIPTION_NOT_FOUND_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private TypeRepository typeRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @Mock
    private JwtParserUtil jwtParserUtil;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private final UUID elementId = UUID.randomUUID();
    private final UUID subscriptionId = UUID.randomUUID();
    private final String email = "user@example.com";
    private final String token = "token";
    private final String typeName = "TYPE";

    private SubscriptionDTO requestDto;
    private SubscriptionEntity entity;
    private TypeEntity typeEntity;

    @BeforeEach
    void setUp() {
        requestDto = new SubscriptionDTO();
        requestDto.setId(subscriptionId);
        requestDto.setElementId(elementId);
        requestDto.setType(typeName);
        requestDto.setEmail(email);
        entity = new SubscriptionEntity();
        entity.setId(subscriptionId);
        entity.setElementId(elementId);
        entity.setEmail(email);
        typeEntity = new TypeEntity();
        typeEntity.setName(typeName);
    }

    @Test
    void addSubscription_success() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(typeRepository.findByName(typeName)).thenReturn(Optional.of(typeEntity));
        when(subscriptionRepository.existsByEmailAndElementIdAndTypeEntity(email, elementId, typeEntity)).thenReturn(false);
        when(subscriptionMapper.toEntity(requestDto)).thenReturn(entity);

        subscriptionService.addSubscription(token, requestDto);

        ArgumentCaptor<SubscriptionEntity> captor = ArgumentCaptor.forClass(SubscriptionEntity.class);
        verify(subscriptionRepository).save(captor.capture());
        SubscriptionEntity saved = captor.getValue();
        assertEquals(typeEntity, saved.getTypeEntity(), "TypeEntity should be set");
        assertEquals(email, saved.getEmail());
        assertEquals(elementId, saved.getElementId());
    }

    @Test
    void addSubscription_accessDenied() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn("other@example.com");
        CustomAccessDeniedException ex = assertThrows(CustomAccessDeniedException.class,
                () -> subscriptionService.addSubscription(token, requestDto));
        assertEquals(ACCESS_DENIED_EXCEPTION, ex.getMessage());
    }

    @Test
    void addSubscription_typeNotSupported() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(typeRepository.findByName(typeName)).thenReturn(Optional.empty());
        ConflictException ex = assertThrows(ConflictException.class,
                () -> subscriptionService.addSubscription(token, requestDto));
        assertTrue(ex.getMessage().contains("Тип объекта не поддерживается"));
    }

    @Test
    void addSubscription_alreadyExists() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(typeRepository.findByName(typeName)).thenReturn(Optional.of(typeEntity));
        when(subscriptionRepository.existsByEmailAndElementIdAndTypeEntity(email, elementId, typeEntity)).thenReturn(true);
        ConflictException ex = assertThrows(ConflictException.class,
                () -> subscriptionService.addSubscription(token, requestDto));
        assertTrue(ex.getMessage().contains("Подписка на объект уже есть"));
    }

    @Test
    void getAllSubscriptions_success() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        SubscriptionEntity sub = new SubscriptionEntity();
        sub.setId(subscriptionId);
        sub.setEmail(email);
        sub.setElementId(elementId);
        Pageable pageable = PageRequest.of(1, 2);
        Page<SubscriptionEntity> page = new PageImpl<>(List.of(sub), pageable, 1);
        when(subscriptionRepository.findByEmail(email, pageable)).thenReturn(page);

        SubscriptionDTO dto = new SubscriptionDTO();
        dto.setId(subscriptionId);
        dto.setElementId(elementId);
        dto.setType(typeName);
        dto.setEmail(email);
        when(subscriptionMapper.toDto(sub)).thenReturn(dto);

        ListSubscriptionsDTO result = subscriptionService.getAllSubscriptions(token, email, 1, 2);

        assertEquals(1, result.getSubscriptions().size());
        assertEquals(dto, result.getSubscriptions().get(0));
        assertEquals(1, result.getCurrentPage());
        assertEquals(3, result.getTotalItems());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void getAllSubscriptions_accessDenied() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn("other@example.com");
        assertThrows(CustomAccessDeniedException.class,
                () -> subscriptionService.getAllSubscriptions(token, email, 0, 5));
    }

    @Test
    void deleteAllSubscriptions_success() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);

        subscriptionService.deleteAllSubscriptions(token, email);

        verify(subscriptionRepository).deleteByEmail(email);
    }

    @Test
    void deleteAllSubscriptions_accessDenied() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn("other@example.com");
        assertThrows(CustomAccessDeniedException.class,
                () -> subscriptionService.deleteAllSubscriptions(token, email));
    }

    @Test
    void deleteSubscriptionByElementId_success() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(subscriptionRepository.existsByEmailAndElementId(email, elementId)).thenReturn(true);

        subscriptionService.deleteSubscriptionByElementId(token, email, elementId);

        verify(subscriptionRepository).deleteByEmailAndElementId(email, elementId);
    }

    @Test
    void deleteSubscriptionByElementId_notFound() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(subscriptionRepository.existsByEmailAndElementId(email, elementId)).thenReturn(false);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.deleteSubscriptionByElementId(token, email, elementId));
        assertEquals(SUBSCRIPTION_NOT_FOUND_EXCEPTION, ex.getMessage());
    }

    @Test
    void deleteSubscriptionByElementId_accessDenied() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn("other@example.com");
        assertThrows(CustomAccessDeniedException.class,
                () -> subscriptionService.deleteSubscriptionByElementId(token, email, elementId));
    }

    @Test
    void deleteSubscriptionBySubscriptionId_success() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(subscriptionRepository.existsByIdAndEmail(subscriptionId, email)).thenReturn(true);

        subscriptionService.deleteSubscriptionBySubscriptionId(token, email, subscriptionId);

        verify(subscriptionRepository).deleteByIdAndEmail(subscriptionId, email);
    }

    @Test
    void deleteSubscriptionBySubscriptionId_notFound() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(subscriptionRepository.existsByIdAndEmail(subscriptionId, email)).thenReturn(false);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.deleteSubscriptionBySubscriptionId(token, email, subscriptionId));
        assertEquals(SUBSCRIPTION_NOT_FOUND_EXCEPTION, ex.getMessage());
    }

    @Test
    void deleteSubscriptionBySubscriptionId_accessDenied() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn("other@example.com");
        assertThrows(CustomAccessDeniedException.class,
                () -> subscriptionService.deleteSubscriptionBySubscriptionId(token, email, subscriptionId));
    }

    @Test
    void isSubscriptionExisted_returnsTrue() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(subscriptionRepository.existsByEmailAndElementId(email, elementId)).thenReturn(true);

        boolean exists = subscriptionService.isSubscriptionExisted(token, elementId);

        assertTrue(exists);
    }

    @Test
    void isSubscriptionExisted_returnsFalse() {
        when(jwtParserUtil.extractEmailFromJwt(token)).thenReturn(email);
        when(subscriptionRepository.existsByEmailAndElementId(email, elementId)).thenReturn(false);

        boolean exists = subscriptionService.isSubscriptionExisted(token, elementId);

        assertFalse(exists);
    }
}
