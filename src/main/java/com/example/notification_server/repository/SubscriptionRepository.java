package com.example.notification_server.repository;

import com.example.notification_server.model.entity.SubscriptionEntity;
import com.example.notification_server.model.entity.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, UUID> {
    void deleteByEmailAndElementIdAndTypeEntity(String email, UUID elementId, TypeEntity typeEntity);

    boolean existsByEmailAndElementIdAndTypeEntity(String email, UUID elementId, TypeEntity typeEntity);
}