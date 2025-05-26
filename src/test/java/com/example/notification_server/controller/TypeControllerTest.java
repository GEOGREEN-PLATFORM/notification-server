package com.example.notification_server.controller;


import com.example.notification_server.service.TypeService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TypeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TypeService typeService;

    @InjectMocks
    private TypeController typeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(typeController).build();
    }

    @Test
    void getTypes_returnsTypeList() throws Exception {
        List<String> types = List.of("A", "B", "C");
        when(typeService.getAllTypes()).thenReturn(types);

        mockMvc.perform(get("/notification/type")
                        .header("Authorization", "Bearer test-token")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("A"))
                .andExpect(jsonPath("$[1]").value("B"))
                .andExpect(jsonPath("$[2]").value("C"));
    }

    @Test
    void getTypes_returnsEmptyList() throws Exception {
        when(typeService.getAllTypes()).thenReturn(List.of());

        mockMvc.perform(get("/notification/type")
                        .header("Authorization", "Bearer test-token")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}
