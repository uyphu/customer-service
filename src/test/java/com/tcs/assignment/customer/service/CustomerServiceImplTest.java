package com.tcs.assignment.customer.service;

import com.tcs.assignment.customer.constant.CustomerConstants;
import com.tcs.assignment.customer.dto.CustomerRequest;
import com.tcs.assignment.customer.dto.CustomerResponse;
import com.tcs.assignment.customer.entity.Customer;
import com.tcs.assignment.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    private CustomerRepository customerRepository;
    private MessageSource messageSource;
    private CustomerServiceImpl customerService;

    private final UUID id = UUID.randomUUID();
    private Customer customer;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        messageSource = mock(MessageSource.class);
        customerService = new CustomerServiceImpl(customerRepository, messageSource);

        customer = new Customer(
                id,
                "Test User",
                "test@example.com",
                BigDecimal.valueOf(500),
                LocalDateTime.now()
        );

        when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(messageSource.getMessage(eq("customer.error.email-exists"), any(), any()))
            .thenReturn("customer.error.email-exists");
        when(messageSource.getMessage(eq("customer.error.name-exists"), any(), any()))
                .thenReturn("customer.error.name-exists");
        when(messageSource.getMessage(eq("customer.error.not-found"), any(), any()))
                .thenReturn("Customer not found");
    }

    @Test
    void testCreate_Success() {
        CustomerRequest request = new CustomerRequest("John", "john@example.com", new BigDecimal("500"), LocalDateTime.now());
        UUID generatedId = UUID.randomUUID();
        Customer saved = new Customer(generatedId, "John", "john@example.com", new BigDecimal("500"), request.getLastPurchaseDate());

        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByName("John")).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertEquals("John", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("Silver", response.getTier());
        assertEquals(generatedId, response.getId());
        verify(customerRepository).save(any());
    }

    @Test
    void testCreate_EmailExists_ShouldThrowConflict() {
        CustomerRequest request = new CustomerRequest("John", "john@example.com", new BigDecimal("500"), LocalDateTime.now());
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new Customer()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> customerService.create(request));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("customer.error.email-exists", exception.getReason());
    }

    @Test
    void testCreate_NameExists_ShouldThrowConflict() {
        CustomerRequest request = new CustomerRequest("John", "john@example.com", new BigDecimal("500"), LocalDateTime.now());
        when(customerRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByName("John")).thenReturn(Optional.of(new Customer()));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> customerService.create(request));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("customer.error.name-exists", exception.getReason());
    }

    @Test
    void testGetById_Success() {
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getById(id);

        assertNotNull(response);
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testGetById_NotFound() {
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> customerService.getById(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

        @Test
    void testGetByName_Success() {
        when(customerRepository.findByName("Test User")).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getByName("Test User");

        assertNotNull(response);
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testGetByName_NotFound() {
        when(customerRepository.findByName("Unknown")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.getByName("Unknown"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    void testGetByEmail_Success() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getByEmail("test@example.com");

        assertNotNull(response);
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testGetByEmail_NotFound() {
        when(customerRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.getByEmail("missing@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

        @Test
    void testGetByNameOrEmail_BothMatch_Success() {
        when(customerRepository.findByNameAndEmail("Test User", "test@example.com"))
                .thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getByNameOrEmail("Test User", "test@example.com");

        assertNotNull(response);
        assertEquals("Test User", response.getName());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testGetByNameOrEmail_OnlyNameMatch_Success() {
        when(customerRepository.findByName("Test User")).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getByNameOrEmail("Test User", null);

        assertNotNull(response);
        assertEquals("Test User", response.getName());
    }

    @Test
    void testGetByNameOrEmail_OnlyEmailMatch_Success() {
        when(customerRepository.findByEmail("test@example.com")).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getByNameOrEmail(null, "test@example.com");

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testGetByNameOrEmail_BothMatch_NotFound() {
        when(customerRepository.findByNameAndEmail("Unknown", "unknown@example.com"))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.getByNameOrEmail("Unknown", "unknown@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    void testGetByNameOrEmail_OnlyName_NotFound() {
        when(customerRepository.findByName("Missing")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.getByNameOrEmail("Missing", null));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    void testGetByNameOrEmail_OnlyEmail_NotFound() {
        when(customerRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.getByNameOrEmail(null, "missing@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    void testGetByNameOrEmail_MissingParams_ShouldThrowBadRequest() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> customerService.getByNameOrEmail(null, null));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("customer.error.missing-query", exception.getReason());
    }

    @Test
    void testUpdate_Success() {
        CustomerRequest request = new CustomerRequest("Updated Name", "updated@example.com", new BigDecimal("999"), LocalDateTime.now());

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByName("Updated Name")).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.update(id, request);

        assertEquals("Updated Name", response.getName());
        assertEquals("updated@example.com", response.getEmail());
        assertEquals("Silver", response.getTier());
    }

    @Test
    void testUpdate_EmailExists_ShouldThrowConflict() {
        CustomerRequest request = new CustomerRequest("Updated Name", "duplicate@example.com", new BigDecimal("500"), LocalDateTime.now());
        Customer existingOther = new Customer(UUID.randomUUID(), "Other", "duplicate@example.com", BigDecimal.ZERO, LocalDateTime.now());

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("duplicate@example.com")).thenReturn(Optional.of(existingOther));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> customerService.update(id, request));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("customer.error.email-exists", exception.getReason());
    }

    @Test
    void testUpdate_NameExists_ShouldThrowConflict() {
        CustomerRequest request = new CustomerRequest("Existing Name", "updated@example.com", new BigDecimal("500"), LocalDateTime.now());
        Customer existingOther = new Customer(UUID.randomUUID(), "Existing Name", "someone@example.com", BigDecimal.ZERO, LocalDateTime.now());

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail("updated@example.com")).thenReturn(Optional.empty());
        when(customerRepository.findByName("Existing Name")).thenReturn(Optional.of(existingOther));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> customerService.update(id, request));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("customer.error.name-exists", exception.getReason());
    }

    @Test
    void testUpdate_NotFound_ShouldThrowNotFound() {
        CustomerRequest request = new CustomerRequest("Any", "any@example.com", new BigDecimal("500"), LocalDateTime.now());

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> customerService.update(id, request));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());
    }

    @Test
    void testDelete_Success() {
        when(customerRepository.existsById(id)).thenReturn(true);
        doNothing().when(customerRepository).deleteById(id);

        assertDoesNotThrow(() -> customerService.delete(id));
        verify(customerRepository).deleteById(id);
    }

    @Test
    void testDelete_NotFound_ShouldThrowNotFound() {
        when(customerRepository.existsById(id)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> customerService.delete(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Customer not found", exception.getReason());

        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void testCreate_TierShouldBePlatinum() {
        CustomerRequest request = new CustomerRequest(
                "PlatinumUser",
                "platinum@example.com",
                CustomerConstants.PLATINUM_THRESHOLD.add(BigDecimal.ONE), // >= 10000
                LocalDateTime.now().minusMonths(CustomerConstants.PLATINUM_MONTHS_LIMIT - 1) // within 6 months
        );
        UUID generatedId = UUID.randomUUID();
        Customer saved = new Customer(generatedId, request.getName(), request.getEmail(),
                request.getAnnualSpend(), request.getLastPurchaseDate());

        when(customerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(customerRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertEquals("Platinum", response.getTier());
    }

    @Test
    void testCreate_TierShouldBeGold() {
        CustomerRequest request = new CustomerRequest(
                "GoldUser",
                "gold@example.com",
                CustomerConstants.GOLD_THRESHOLD.add(BigDecimal.ONE), // >= 1000
                LocalDateTime.now().minusMonths(CustomerConstants.GOLD_MONTHS_LIMIT - 1) // within 12 months
        );
        UUID generatedId = UUID.randomUUID();
        Customer saved = new Customer(generatedId, request.getName(), request.getEmail(),
                request.getAnnualSpend(), request.getLastPurchaseDate());

        when(customerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(customerRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertEquals("Gold", response.getTier());
    }

    @Test
    void testCreate_TierShouldBeSilverWhenNoAnnualSpend() {
        CustomerRequest request = new CustomerRequest(
                "NoSpendUser",
                "nospend@example.com",
                null,
                LocalDateTime.now()
        );
        UUID generatedId = UUID.randomUUID();
        Customer saved = new Customer(generatedId, request.getName(), request.getEmail(),
                null, request.getLastPurchaseDate());

        when(customerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(customerRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertEquals("Silver", response.getTier());
    }

    @Test
    void testCreate_TierShouldBeSilverWhenConditionsNotMet() {
        CustomerRequest request = new CustomerRequest(
                "LowUser",
                "low@example.com",
                new BigDecimal("999.99"), // less than 1000
                LocalDateTime.now()
        );
        UUID generatedId = UUID.randomUUID();
        Customer saved = new Customer(generatedId, request.getName(), request.getEmail(),
                request.getAnnualSpend(), request.getLastPurchaseDate());

        when(customerRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(customerRepository.findByName(request.getName())).thenReturn(Optional.empty());
        when(customerRepository.save(any())).thenReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertEquals("Silver", response.getTier());
    }

}
