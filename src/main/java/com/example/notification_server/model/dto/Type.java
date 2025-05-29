package com.example.notification_server.model.dto;

import lombok.Getter;

@Getter
public enum Type {
    EVENT("Мероприятие"),
    USER_MARKER("Заявка"),
    POINT("Очаг");

    private final String name;

    Type(String s) {
        this.name = s;
    }
}