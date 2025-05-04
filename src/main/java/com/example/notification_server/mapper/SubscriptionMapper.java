package com.example.notification_server.mapper;

import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.model.entity.SubscriptionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionEntity toEntity(SubscriptionDTO request);
}