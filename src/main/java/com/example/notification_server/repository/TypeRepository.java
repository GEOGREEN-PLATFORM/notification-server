package com.example.notification_server.repository;

import com.example.notification_server.model.entity.TypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TypeRepository extends JpaRepository<TypeEntity, Integer> {
    Optional<TypeEntity> findByName(String name);
}