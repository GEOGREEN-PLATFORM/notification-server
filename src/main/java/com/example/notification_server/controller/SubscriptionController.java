package com.example.notification_server.controller;

import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.service.SubscriptionService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.notification_server.util.AuthorizationStringUtil.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification/subscribe")
@SecurityRequirement(name = AUTHORIZATION)
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<Void> addSubscription(@Valid @RequestBody SubscriptionDTO request) {
        subscriptionService.addSubscription(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteSubscription(@Valid @RequestBody SubscriptionDTO request) {
        subscriptionService.deleteSubscription(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}