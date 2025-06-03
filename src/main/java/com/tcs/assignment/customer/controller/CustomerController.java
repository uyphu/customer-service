package com.tcs.assignment.customer.controller;

import com.tcs.assignment.customer.dto.CustomerRequest;
import com.tcs.assignment.customer.dto.CustomerResponse;
import com.tcs.assignment.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer API", description = "CRUD operations and tier calculation for customers")
public class CustomerController {

    private final CustomerService service;

    @Operation(
        summary = "Create a new customer",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CustomerRequest.class),
                examples = @ExampleObject(value = """
                    {
                        "name": "John Doe",
                        "email": "john@example.com",
                        "annualSpend": 1200.0,
                        "lastPurchaseDate": "2024-10-01T10:30:00"
                    }
                """)
            )
        ),
        responses = {
            @ApiResponse(responseCode = "201", description = "Customer created",
                content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                content = @Content(schema = @Schema(example = "{\"error\":\"email: must be a well-formed email address\"}")))
        }
    )
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get customer by ID")
    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @Operation(
        summary = "Get customer by name, email, or both",
        description = "Provide either 'name', 'email', or both as query parameters. If both are provided, both will be matched (AND condition).",
        parameters = {
            @Parameter(name = "name", description = "Customer name to search by", required = false),
            @Parameter(name = "email", description = "Customer email to search by", required = false)
        }
    )
    @GetMapping
    public CustomerResponse getByQuery(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email
    ) {
        return service.getByNameOrEmail(name, email);
    }

    @Operation(summary = "Update customer by ID")
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable UUID id, @RequestBody CustomerRequest request) {
        return service.update(id, request);
    }

    @Operation(summary = "Delete customer by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
