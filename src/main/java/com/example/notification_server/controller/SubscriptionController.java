package com.example.notification_server.controller;

import com.example.notification_server.model.dto.ListSubscriptionsDTO;
import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.service.SubscriberNotificationService;
import com.example.notification_server.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.example.notification_server.util.AuthorizationStringUtil.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification/subscribe")
@SecurityRequirement(name = AUTHORIZATION)
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final SubscriberNotificationService subscriberNotificationService;

    @Operation(
            summary = "Подписка на элемент"
    )
    @PostMapping
    public ResponseEntity<Void> addSubscription(@RequestHeader(AUTHORIZATION) String token, @Valid @RequestBody SubscriptionDTO request) {
        subscriptionService.addSubscription(token, request);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Получение всех подписок"
    )
    @GetMapping(path = "/get-all/{email}", params = {"page", "size"})
    public ResponseEntity<ListSubscriptionsDTO> getAllSubscriptions(@RequestHeader(AUTHORIZATION) String token,
                                                                    @NotNull @RequestParam("page") int page,
                                                                    @NotNull @RequestParam("size") int size,
                                                                    @PathVariable("email") String email) {
        return ResponseEntity.ok(subscriptionService.getAllSubscriptions(token, email, page, size));
    }

    @Operation(
            summary = "Отказ от всех подписок"
    )
    @DeleteMapping("/{email}/delete-all")
    public ResponseEntity<Void> deleteAllSubscriptions(@RequestHeader(AUTHORIZATION) String token, @PathVariable("email") String email) {
        subscriptionService.deleteAllSubscriptions(token, email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Отказ от подписки на элемент по ID элемента"
    )
    @DeleteMapping("/{email}/delete-by-element-id/{elementId}")
    public ResponseEntity<Void> deleteSubscriptionByElementId(@RequestHeader(AUTHORIZATION) String token, @PathVariable("email") String email, @PathVariable("elementId") UUID elementId) {
        subscriptionService.deleteSubscriptionByElementId(token, email, elementId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Отказ от подписки на элемент по ID подписки"
    )
    @DeleteMapping("/{email}/delete-by-subscription-id/{subscriptionId}")
    public ResponseEntity<Void> deleteSubscriptionBySubscriptionId(@RequestHeader(AUTHORIZATION) String token, @PathVariable("email") String email, @PathVariable("subscriptionId") UUID subscriptionId) {
        subscriptionService.deleteSubscriptionBySubscriptionId(token, email, subscriptionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(
            summary = "Верификация подписки на элемент"
    )
    @GetMapping("/{elementId}")
    public ResponseEntity<Boolean> isSubscriptionExisted(@RequestHeader(AUTHORIZATION) String token, @PathVariable("elementId") UUID elementId) {
        return ResponseEntity.ok(subscriptionService.isSubscriptionExisted(token, elementId));
    }
}