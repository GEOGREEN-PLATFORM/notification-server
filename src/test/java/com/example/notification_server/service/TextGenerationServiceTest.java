package com.example.notification_server.service;


import com.example.notification_server.model.dto.Type;
import com.example.notification_server.model.dto.UpdateElementDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextGenerationServiceTest {

    private TextGenerationService textService;
    private UpdateElementDTO dto;
    private UUID elementId;
    private String status;

    @BeforeEach
    void setUp() {
        textService = new TextGenerationService();
        elementId = UUID.randomUUID();
        status = "COMPLETED";
    }

    @Test
    void generateTitle_shouldPrefixWithServiceNameAndType() {
        String typeName = "CUSTOM_TYPE";
        dto = new UpdateElementDTO(elementId, typeName, status, OffsetDateTime.now());
        String title = textService.generateTitle(dto);

        assertEquals("GEOGREEN. Обновление: CUSTOM_TYPE", title, "Title should include service name and type");
    }

    @Test
    void generateText_forUserMarker_shouldContainStatusAndLink() {
        String typeName = Type.USER_MARKER.getName();
        dto = new UpdateElementDTO(elementId, typeName, status, OffsetDateTime.now());
        String text = textService.generateText(dto);

        assertTrue(text.contains("Обновления статуса заявки: " + status), "Text should mention the status");
        String expectedLink = String.format("http://217.198.13.249:30099/account/user?checkReportDetail=%s", elementId);
        assertTrue(text.contains(expectedLink), "Text should contain the hotbeds link with elementId");
    }

    @Test
    void generateText_forPoint_shouldContainStatusAndLink() {
        String typeName = Type.POINT.getName();
        dto = new UpdateElementDTO(elementId, typeName, status, OffsetDateTime.now());
        String text = textService.generateText(dto);

        assertTrue(text.contains("Обновления статуса очага: " + status), "Text should mention the status");
        String expectedLink = String.format("http://217.198.13.249:30099/hotbeds/%s", elementId);
        assertTrue(text.contains(expectedLink), "Text should contain the account user link with elementId");
    }

    @Test
    void generateText_forUnknownType_shouldReturnEmpty() {
        String typeName = "UNKNOWN";
        dto = new UpdateElementDTO(elementId, typeName, status, OffsetDateTime.now());
        String text = textService.generateText(dto);

        assertEquals("", text, "Text should be empty for unknown types");
    }
}
