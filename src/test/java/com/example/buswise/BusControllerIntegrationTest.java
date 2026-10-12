package com.example.buswise;

import com.example.buswise.dto.BusRequestDTO;
import com.example.buswise.entity.BusStatus;
import com.example.buswise.entity.FuelType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BusControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/buses should return list of buses with HTTP 200")
    void testGetBuses() throws Exception {
        mockMvc.perform(get("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("GET /api/dashboard/summary should return computed summary metrics")
    void testGetDashboardSummary() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalBuses").isNumber());
    }

    @Test
    @DisplayName("POST /api/buses with invalid payload should return HTTP 400 Bad Request")
    void testCreateBus_ValidationFailure() throws Exception {
        BusRequestDTO invalidDto = new BusRequestDTO();
        invalidDto.setRegistrationNumber(""); // Blank registration
        invalidDto.setSeatingCapacity(-5);   // Negative capacity

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").exists());
    }
}
