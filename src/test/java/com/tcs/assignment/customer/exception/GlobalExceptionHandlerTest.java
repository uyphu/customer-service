package com.tcs.assignment.customer.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test-uri");
    }

    @Test
    void testHandleValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("objectName", "name", "must not be blank");
        FieldError fieldError2 = new FieldError("objectName", "email", "must be a valid email");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<List<ErrorResponse>> response = exceptionHandler.handleValidationErrors(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertTrue(response.getBody().get(0).getError().contains("name"));
        assertTrue(response.getBody().get(1).getError().contains("email"));
        assertEquals("/test-uri", response.getBody().get(0).getPath());
    }

    @Test
    void testHandleUnexpected() {
        Exception ex = new RuntimeException("Something went wrong");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUnexpected(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().getError());
        assertEquals("/test-uri", response.getBody().getPath());
    }

    @Test
    void testHandleCustomerNotFound() {
        CustomerNotFoundException ex = new CustomerNotFoundException("Customer not found");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCustomerNotFound(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found", response.getBody().getError());
        assertEquals("/test-uri", response.getBody().getPath());
    }

    @Test
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid input provided");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid input provided", response.getBody().getError());
        assertEquals("/test-uri", response.getBody().getPath());
    }

}
