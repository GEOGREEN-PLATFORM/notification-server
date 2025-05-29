package com.example.notification_server.mapper;

import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.model.entity.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    SubscriptionEntity toEntity(SubscriptionDTO request);

    @Mapping(target = "type", source = "entity.typeEntity.name")
    SubscriptionDTO toDto(SubscriptionEntity entity);
}