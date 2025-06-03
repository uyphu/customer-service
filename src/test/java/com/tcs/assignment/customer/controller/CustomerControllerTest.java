package com.tcs.assignment.customer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tcs.assignment.customer.dto.CustomerRequest;
import com.tcs.assignment.customer.dto.CustomerResponse;
import com.tcs.assignment.customer.service.CustomerService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    void testCreateCustomer() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "john@example.com", new BigDecimal("1200"), LocalDateTime.now());
        CustomerResponse response = new CustomerResponse(UUID.randomUUID(), "John", "john@example.com", new BigDecimal("1200"), request.getLastPurchaseDate(), "Gold");

        when(customerService.create(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testGetCustomerById() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerResponse response = new CustomerResponse(id, "John", "john@example.com", new BigDecimal("1200"), LocalDateTime.now(), "Gold");

        when(customerService.getById(id)).thenReturn(response);

        mockMvc.perform(get("/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void testGetCustomerByQueryName() throws Exception {
        CustomerResponse response = new CustomerResponse(UUID.randomUUID(), "John", "john@example.com", new BigDecimal("1200"), LocalDateTime.now(), "Gold");

        when(customerService.getByNameOrEmail("John", null)).thenReturn(response);

        mockMvc.perform(get("/customers?name=John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void testUpdateCustomer() throws Exception {
        UUID id = UUID.randomUUID();
        CustomerRequest request = new CustomerRequest("John", "john@example.com", new BigDecimal("1200"), LocalDateTime.now());
        CustomerResponse response = new CustomerResponse(id, "John", "john@example.com", new BigDecimal("1200"), request.getLastPurchaseDate(), "Gold");

        when(customerService.update(eq(id), any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(put("/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void testDeleteCustomer() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/customers/" + id))
                .andExpect(status().isNoContent());
    }
}
