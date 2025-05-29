package com.example.notification_server.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListSubscriptionsDTO {
    private List<SubscriptionDTO> subscriptions;
    private int currentPage;
    private long totalItems;
    private int totalPages;
}