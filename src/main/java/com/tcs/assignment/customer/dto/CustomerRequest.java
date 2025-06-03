package com.tcs.assignment.customer.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CustomerRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    private BigDecimal annualSpend;
    private LocalDateTime lastPurchaseDate;
}

