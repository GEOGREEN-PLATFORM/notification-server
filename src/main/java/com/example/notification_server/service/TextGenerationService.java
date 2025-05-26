package com.example.notification_server.service;

import com.example.notification_server.model.dto.Type;
import com.example.notification_server.model.dto.UpdateElementDTO;
import org.springframework.stereotype.Service;

@Service
public class TextGenerationService {
    public String generateTitle(UpdateElementDTO updateElementDTO) {
        return String.format("GEOGREEN. Обновление: %s", updateElementDTO.getType());
    }

    public String generateText(UpdateElementDTO updateElementDTO) {
        var type = updateElementDTO.getType();
        String result = "";
        if (Type.USER_MARKER.getName().equals(type)) {
            result = String.format("Обновления статуса заявки: %s", updateElementDTO.getStatus());
            result += String.format("\n Посмотреть изменения по ссылке: http://217.198.13.249:30099/hotbeds/%s", updateElementDTO.getElementId());
        }
        if (Type.POINT.getName().equals(type)) {
            result = String.format("Обновления статуса очага: %s", updateElementDTO.getStatus());
            result += String.format("\n Посмотреть изменения по ссылке: http://217.198.13.249:30099/account/user?checkReportDetail=%s", updateElementDTO.getElementId());

        }
        return result;
    }
}
