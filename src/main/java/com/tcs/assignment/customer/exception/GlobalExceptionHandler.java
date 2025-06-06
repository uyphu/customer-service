package com.tcs.assignment.customer.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<List<ErrorResponse>> handleValidationErrors(
                        MethodArgumentNotValidException ex,
                        HttpServletRequest request) {
                List<ErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                                .map(fieldError -> ErrorResponse.builder()
                                                .timestamp(LocalDateTime.now())
                                                .status(HttpStatus.BAD_REQUEST.value())
                                                .error(fieldError.getField() + ": " + fieldError.getDefaultMessage())
                                                .path(request.getRequestURI())
                                                .build())
                                .collect(Collectors.toList());

                return ResponseEntity.badRequest().body(errors);
        }

        @ExceptionHandler(CustomerNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleCustomerNotFound(
                        CustomerNotFoundException ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.NOT_FOUND.value())
                                .error(ex.getMessage())
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleUnexpected(
                        Exception ex,
                        HttpServletRequest request) {
                ErrorResponse error = ErrorResponse.builder()
                                .timestamp(LocalDateTime.now())
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .error("Internal server error")
                                .path(request.getRequestURI())
                                .build();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                IllegalArgumentException ex,
                HttpServletRequest request
        ) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.badRequest().body(error);
        }
}
