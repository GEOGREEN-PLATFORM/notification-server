package com.example.notification_server.repository;

import com.example.notification_server.model.entity.SubscriptionEntity;
import com.example.notification_server.model.entity.TypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    boolean existsByEmailAndElementIdAndTypeEntity(String email, UUID elementId, TypeEntity typeEntity);

    Page<SubscriptionEntity> findByEmail(String email, Pageable pageable);

    Page<SubscriptionEntity> findByElementId(UUID elementId, Pageable pageable);

    void deleteByEmail(String email);

    void deleteByEmailAndElementId(String email, UUID elementId);

    boolean existsByEmailAndElementId(String email, UUID elementId);

    void deleteByIdAndEmail(UUID id, String email);

    boolean existsByIdAndEmail(UUID id, String email);
}