package com.example.notification_server.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private UUID id;
    @NotEmpty
    @Email
    private String email;
    @NotNull
    private UUID elementId;
    @NotNull
    private String type;
}