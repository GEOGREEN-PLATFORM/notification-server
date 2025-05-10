package com.example.notification_server.service;

import com.example.notification_server.model.dto.Type;
import com.example.notification_server.model.dto.UpdateElementDTO;
import org.springframework.stereotype.Service;

@Service
public class TextGenerationService {
    public String generateTitle(UpdateElementDTO updateElementDTO) {
        return String.format("Обровление: %s", updateElementDTO.getType());
    }

    public String generateText(UpdateElementDTO updateElementDTO) {
        var type = updateElementDTO.getType();
        String result = "";
        if (Type.USER_MARKER.getName().equals(type)) {
            result = String.format("Обновления статуса заявки: %s", updateElementDTO.getStatus());
        }
        if (Type.EVENT.getName().equals(type)) {
            result = String.format("Обновление мероприятия.\nCтатус: %s\nДата: %s",
                    updateElementDTO.getStatus(), updateElementDTO.getDate().toLocalDate().toString());
        }
        if (Type.POINT.getName().equals(type)) {
            result = String.format("Обновления статуса очага: %s", updateElementDTO.getStatus());
        }
        return result;
    }
}
