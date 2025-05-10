package com.example.notification_server.controller;

import com.example.notification_server.service.TypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.notification_server.util.AuthorizationStringUtil.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification/type")
@SecurityRequirement(name = AUTHORIZATION)
public class TypeController {
    private final TypeService typeService;

    @GetMapping
    public ResponseEntity<List<String>> getTypes() {
        return ResponseEntity.ok(typeService.getAllTypes());
    }
}