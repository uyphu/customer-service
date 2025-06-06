package com.tcs.assignment.customer.service;

import com.tcs.assignment.customer.dto.CustomerRequest;
import com.tcs.assignment.customer.dto.CustomerResponse;
import com.tcs.assignment.customer.entity.Customer;
import com.tcs.assignment.customer.exception.CustomerNotFoundException;
import com.tcs.assignment.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
                .thenReturn("Customer not found");
    }

    @Test
    void testCreate_Success() {
        CustomerRequest request = new CustomerRequest("John", "john@example.com", new BigDecimal("500"), LocalDateTime.now());
        UUID generatedId = UUID.randomUUID();
        Customer saved = new Customer(generatedId, "John", "john@example.com", new BigDecimal("500"), request.getLastPurchaseDate());

        when(customerRepository.save(any())).thenReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertEquals("John", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("Silver", response.getTier());
        assertEquals(generatedId, response.getId());
        verify(customerRepository).save(any());
    }

    @Test
    void testGetById_Success() {
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getById(id);

        assertNotNull(response);
        assertEquals("Test User", response.getName());
    }

    @Test
    void testGetById_NotFound() {
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getById(id));
        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testGetByName_Success() {
        when(customerRepository.findAllByNameIgnoreCase("Test User")).thenReturn(List.of(customer));

        CustomerResponse response = customerService.getByName("Test User");

        assertEquals("Test User", response.getName());
    }

    @Test
    void testGetByName_NotFound() {
        when(customerRepository.findAllByNameIgnoreCase("Unknown")).thenReturn(Collections.emptyList());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getByName("Unknown"));
        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testGetByEmail_Success() {
        when(customerRepository.findAllByEmailIgnoreCase("test@example.com")).thenReturn(List.of(customer));

        CustomerResponse response = customerService.getByEmail("test@example.com");

        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testGetByEmail_NotFound() {
        when(customerRepository.findAllByEmailIgnoreCase("missing@example.com")).thenReturn(Collections.emptyList());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getByEmail("missing@example.com"));
        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testGetByNameOrEmail_BothMatch_Success() {
        when(customerRepository.findAllByNameAndEmailAllIgnoreCase("Test User", "test@example.com"))
                .thenReturn(List.of(customer));

        CustomerResponse response = customerService.getByNameOrEmail("Test User", "test@example.com");

        assertEquals("Test User", response.getName());
    }

    @Test
    void testGetByNameOrEmail_OnlyName_Success() {
        when(customerRepository.findAllByNameIgnoreCase("Test User")).thenReturn(List.of(customer));

        CustomerResponse response = customerService.getByNameOrEmail("Test User", null);

        assertEquals("Test User", response.getName());
    }

    @Test
    void testGetByNameOrEmail_OnlyEmail_Success() {
        when(customerRepository.findAllByEmailIgnoreCase("test@example.com")).thenReturn(List.of(customer));

        CustomerResponse response = customerService.getByNameOrEmail(null, "test@example.com");

        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testGetByNameOrEmail_NotFound() {
        when(customerRepository.findAllByNameAndEmailAllIgnoreCase("Unknown", "unknown@example.com"))
                .thenReturn(Collections.emptyList());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getByNameOrEmail("Unknown", "unknown@example.com"));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testGetByNameOrEmail_OnlyName_NotFound() {
        when(customerRepository.findAllByNameIgnoreCase("Missing")).thenReturn(Collections.emptyList());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getByNameOrEmail("Missing", null));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testGetByNameOrEmail_OnlyEmail_NotFound() {
        when(customerRepository.findAllByEmailIgnoreCase("missing@example.com")).thenReturn(Collections.emptyList());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.getByNameOrEmail(null, "missing@example.com"));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testUpdate_Success() {
        CustomerRequest request = new CustomerRequest("Updated", "updated@example.com", new BigDecimal("999"), LocalDateTime.now());

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.update(id, request);

        assertEquals("Updated", response.getName());
        assertEquals("updated@example.com", response.getEmail());
    }

    @Test
    void testUpdate_NotFound() {
        CustomerRequest request = new CustomerRequest("X", "x@example.com", BigDecimal.ONE, LocalDateTime.now());

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.update(id, request));

        assertEquals("Customer not found", exception.getMessage());
    }

    @Test
    void testDelete_Success() {
        when(customerRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> customerService.delete(id));
        verify(customerRepository).deleteById(id);
    }

    @Test
    void testDelete_NotFound() {
        when(customerRepository.existsById(id)).thenReturn(false);

        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class,
                () -> customerService.delete(id));

        assertEquals("Customer not found", exception.getMessage());
        verify(customerRepository, never()).deleteById(any());
    }

    @Test
    void testCalculateTier_SilverWhenAnnualSpendIsNull() {
        Customer customer = new Customer(null, "NullSpend", "null@example.com", null, LocalDateTime.now());
        when(customerRepository.save(any())).thenReturn(customer);
        CustomerResponse response = customerService.create(new CustomerRequest(customer.getName(), customer.getEmail(), null, customer.getLastPurchaseDate()));
        assertEquals("Silver", response.getTier());
    }

    @Test
    void testCalculateTier_SilverWhenSpendLessThan1000() {
        CustomerRequest request = new CustomerRequest("LowSpend", "low@example.com", new BigDecimal("999"), LocalDateTime.now());
        Customer saved = new Customer(UUID.randomUUID(), request.getName(), request.getEmail(), request.getAnnualSpend(), request.getLastPurchaseDate());
        when(customerRepository.save(any())).thenReturn(saved);
        CustomerResponse response = customerService.create(request);
        assertEquals("Silver", response.getTier());
    }

    @Test
    void testCalculateTier_SilverWhenGoldCriteriaNotMet() {
        LocalDateTime oldPurchase = LocalDateTime.now().minusMonths(13);
        CustomerRequest request = new CustomerRequest("OldGold", "oldgold@example.com", new BigDecimal("1500"), oldPurchase);
        Customer saved = new Customer(UUID.randomUUID(), request.getName(), request.getEmail(), request.getAnnualSpend(), request.getLastPurchaseDate());
        when(customerRepository.save(any())).thenReturn(saved);
        CustomerResponse response = customerService.create(request);
        assertEquals("Silver", response.getTier());
    }

    @Test
    void testCalculateTier_GoldWhenCriteriaMet() {
        LocalDateTime within12Months = LocalDateTime.now().minusMonths(11);
        CustomerRequest request = new CustomerRequest("ValidGold", "validgold@example.com", new BigDecimal("1500"), within12Months);
        Customer saved = new Customer(UUID.randomUUID(), request.getName(), request.getEmail(), request.getAnnualSpend(), request.getLastPurchaseDate());
        when(customerRepository.save(any())).thenReturn(saved);
        CustomerResponse response = customerService.create(request);
        assertEquals("Gold", response.getTier());
    }

    @Test
    void testCalculateTier_GoldWhenPlatinumPurchaseTooOld() {
        LocalDateTime oldPlatinum = LocalDateTime.now().minusMonths(7);
        CustomerRequest request = new CustomerRequest("OldPlatinum", "oldplat@example.com", new BigDecimal("15000"), oldPlatinum);
        Customer saved = new Customer(UUID.randomUUID(), request.getName(), request.getEmail(), request.getAnnualSpend(), request.getLastPurchaseDate());
        when(customerRepository.save(any())).thenReturn(saved);
        CustomerResponse response = customerService.create(request);
        assertEquals("Gold", response.getTier());
    }

    @Test
    void testCalculateTier_PlatinumWhenCriteriaMet() {
        LocalDateTime within6Months = LocalDateTime.now().minusMonths(3);
        CustomerRequest request = new CustomerRequest("PlatinumUser", "plat@example.com", new BigDecimal("15000"), within6Months);
        Customer saved = new Customer(UUID.randomUUID(), request.getName(), request.getEmail(), request.getAnnualSpend(), request.getLastPurchaseDate());
        when(customerRepository.save(any())).thenReturn(saved);
        CustomerResponse response = customerService.create(request);
        assertEquals("Platinum", response.getTier());
    }

    @Test
    void testCalculateTier_SilverWhenHighSpendButNoPurchaseDate() {
        CustomerRequest request = new CustomerRequest("NoDate", "nodate@example.com", new BigDecimal("20000"), null);
        Customer saved = new Customer(UUID.randomUUID(), request.getName(), request.getEmail(), request.getAnnualSpend(), null);
        when(customerRepository.save(any())).thenReturn(saved);
        CustomerResponse response = customerService.create(request);
        assertEquals("Silver", response.getTier());
    }

    @Test
    void testGetByNameOrEmail_MissingNameAndEmail_ThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.getByNameOrEmail(null, null));

        assertEquals("Customer not found", exception.getMessage());
    }


}
