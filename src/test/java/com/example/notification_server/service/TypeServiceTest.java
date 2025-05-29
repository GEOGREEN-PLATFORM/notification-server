package com.example.notification_server.service;


import com.example.notification_server.model.entity.TypeEntity;
import com.example.notification_server.repository.TypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TypeServiceTest {

    @Mock
    private TypeRepository typeRepository;

    @InjectMocks
    private TypeService typeService;

    private TypeEntity typeA;
    private TypeEntity typeB;

    @BeforeEach
    void setUp() {
        typeA = new TypeEntity();
        typeA.setName("A");
        typeB = new TypeEntity();
        typeB.setName("B");
    }

    @Test
    void getAllTypes_returnsListOfNames() {
        when(typeRepository.findAll()).thenReturn(List.of(typeA, typeB));

        List<String> types = typeService.getAllTypes();

        assertEquals(2, types.size(), "Should return two type names");
        assertEquals("A", types.get(0), "First type name should match");
        assertEquals("B", types.get(1), "Second type name should match");
    }

    @Test
    void getAllTypes_emptyList() {
        when(typeRepository.findAll()).thenReturn(List.of());

        List<String> types = typeService.getAllTypes();

        assertEquals(0, types.size(), "Should return empty list when no types exist");
    }
}
