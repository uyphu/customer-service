package com.tcs.assignment.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String name;
    private String email;
    private BigDecimal annualSpend;
    private LocalDateTime lastPurchaseDate;
    private String tier;
}

