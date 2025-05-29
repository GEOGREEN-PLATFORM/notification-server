package com.example.notification_server.service;

import com.example.notification_server.model.entity.TypeEntity;
import com.example.notification_server.repository.TypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TypeService {
    private final TypeRepository typeRepository;

    public List<String> getAllTypes() {
        return typeRepository.findAll().stream().map(TypeEntity::getName).toList();
    }
}