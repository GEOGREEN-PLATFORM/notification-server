package com.example.notification_server.controller;


import com.example.notification_server.model.dto.ListSubscriptionsDTO;
import com.example.notification_server.model.dto.SubscriptionDTO;
import com.example.notification_server.service.SubscriberNotificationService;
import com.example.notification_server.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriberNotificationService subscriberNotificationService;

    @InjectMocks
    private SubscriptionController controller;

    private ObjectMapper objectMapper;
    private final String token = "Bearer test-token";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void addSubscription_returnsOk() throws Exception {
        SubscriptionDTO dto = new SubscriptionDTO(UUID.randomUUID(), "user@example.com", UUID.randomUUID(), "TYPE");
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/notification/subscribe")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk());

        verify(subscriptionService).addSubscription(eq(token), eq(dto));
    }

    @Test
    void getAllSubscriptions_returnsList() throws Exception {
        String email = "user@example.com";
        int page = 1, size = 2;
        ListSubscriptionsDTO responseDto = ListSubscriptionsDTO.builder()
                .subscriptions(List.of(new SubscriptionDTO(UUID.randomUUID(), "user@example.com", UUID.randomUUID(), "TYPE")))
                .currentPage(page)
                .totalItems(5)
                .totalPages(3)
                .build();
        when(subscriptionService.getAllSubscriptions(eq(token), eq(email), eq(page), eq(size)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/notification/subscribe/get-all/{email}", email)
                        .header("Authorization", token)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subscriptions").isArray())
                .andExpect(jsonPath("$.currentPage").value(page))
                .andExpect(jsonPath("$.totalItems").value(5))
                .andExpect(jsonPath("$.totalPages").value(3));

        verify(subscriptionService).getAllSubscriptions(eq(token), eq(email), eq(page), eq(size));
    }

    @Test
    void deleteAllSubscriptions_returnsOk() throws Exception {
        String email = "user@example.com";
        mockMvc.perform(delete("/notification/subscribe/{email}/delete-all", email)
                        .header("Authorization", token)
                )
                .andExpect(status().isOk());

        verify(subscriptionService).deleteAllSubscriptions(eq(token), eq(email));
    }

    @Test
    void deleteByElementId_returnsOk() throws Exception {
        String email = "user@example.com";
        UUID elementId = UUID.randomUUID();
        mockMvc.perform(delete("/notification/subscribe/{email}/delete-by-element-id/{elementId}", email, elementId)
                        .header("Authorization", token)
                )
                .andExpect(status().isOk());

        verify(subscriptionService).deleteSubscriptionByElementId(eq(token), eq(email), eq(elementId));
    }

    @Test
    void deleteBySubscriptionId_returnsOk() throws Exception {
        String email = "user@example.com";
        UUID subscriptionId = UUID.randomUUID();
        mockMvc.perform(delete("/notification/subscribe/{email}/delete-by-subscription-id/{subscriptionId}", email, subscriptionId)
                        .header("Authorization", token)
                )
                .andExpect(status().isOk());

        verify(subscriptionService).deleteSubscriptionBySubscriptionId(eq(token), eq(email), eq(subscriptionId));
    }

    @Test
    void isSubscriptionExisted_returnsBoolean() throws Exception {
        UUID elementId = UUID.randomUUID();
        when(subscriptionService.isSubscriptionExisted(eq(token), eq(elementId))).thenReturn(true);

        mockMvc.perform(get("/notification/subscribe/{elementId}", elementId)
                        .header("Authorization", token)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(subscriptionService).isSubscriptionExisted(eq(token), eq(elementId));
    }
}
