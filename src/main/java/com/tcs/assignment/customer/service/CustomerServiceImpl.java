package com.tcs.assignment.customer.service;

import com.tcs.assignment.customer.constant.CustomerConstants;
import com.tcs.assignment.customer.dto.CustomerRequest;
import com.tcs.assignment.customer.dto.CustomerResponse;
import com.tcs.assignment.customer.entity.Customer;
import com.tcs.assignment.customer.exception.CustomerNotFoundException;
import com.tcs.assignment.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final MessageSource messageSource;

    private String getMessage(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }

    @Override
    public CustomerResponse create(CustomerRequest request) {
        log.info("Creating customer with email: {}", request.getEmail());

        Customer customer = new Customer(null, request.getName(), request.getEmail(),
                request.getAnnualSpend(), request.getLastPurchaseDate());

        Customer saved = customerRepository.save(customer);
        log.info("Customer created successfully with ID: {}", saved.getId());

        return toResponse(saved);
    }

    @Override
    public CustomerResponse getById(UUID id) {
        log.debug("Fetching customer by ID: {}", id);
        return customerRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("Customer not found with ID: {}", id);
                    throw new CustomerNotFoundException(getMessage("customer.error.not-found"));
                });
    }

    @Override
    public CustomerResponse getByName(String name) {
        log.debug("Fetching customer by name: {}", name);
        List<Customer> customers = customerRepository.findAllByNameIgnoreCase(name);
        if (customers.isEmpty()) {
            log.warn("Customer not found with name: {}", name);
            throw new CustomerNotFoundException(getMessage("customer.error.not-found"));
        }
        return toResponse(customers.get(0)); // Return first match
    }

    @Override
    public CustomerResponse getByEmail(String email) {
        log.debug("Fetching customer by email: {}", email);
        List<Customer> customers = customerRepository.findAllByEmailIgnoreCase(email);
        if (customers.isEmpty()) {
            log.warn("Customer not found with email: {}", email);
            throw new CustomerNotFoundException(getMessage("customer.error.not-found"));
        }
        return toResponse(customers.get(0)); // Return first match
    }

    @Override
    public CustomerResponse getByNameOrEmail(String name, String email) {
        log.debug("Fetching customer by name/email: {} / {}", name, email);

        if (name != null && email != null) {
            List<Customer> customers = customerRepository.findAllByNameAndEmailAllIgnoreCase(name, email);
            if (customers.isEmpty()) {
                log.warn("Customer not found with name/email: {} / {}", name, email);
                throw new CustomerNotFoundException(getMessage("customer.error.not-found"));
            }
            return toResponse(customers.get(0)); // Return first match
        }

        if (name != null) {
            return getByName(name);
        }

        if (email != null) {
            return getByEmail(email);
        }

        log.error("Missing query parameters: name and email");
        throw new IllegalArgumentException(getMessage("customer.error.missing-query"));
    }

    @Override
    public CustomerResponse update(UUID id, CustomerRequest request) {
        log.info("Updating customer ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Customer not found with ID: {}", id);
                    throw new CustomerNotFoundException(getMessage("customer.error.not-found"));
                });

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setAnnualSpend(request.getAnnualSpend());
        customer.setLastPurchaseDate(request.getLastPurchaseDate());

        Customer updated = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", updated.getId());

        return toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting customer with ID: {}", id);
        if (!customerRepository.existsById(id)) {
            log.warn("Customer to delete not found: {}", id);
            throw new CustomerNotFoundException(getMessage("customer.error.not-found"));
        }
        customerRepository.deleteById(id);
        log.info("Customer deleted: {}", id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getAnnualSpend(),
                customer.getLastPurchaseDate(),
                calculateTier(customer)
        );
    }

    private String calculateTier(Customer c) {
        if (c.getAnnualSpend() == null) return CustomerConstants.TIER_SILVER;

        if (c.getAnnualSpend().compareTo(CustomerConstants.PLATINUM_THRESHOLD) >= 0 &&
                c.getLastPurchaseDate() != null &&
                c.getLastPurchaseDate().isAfter(LocalDateTime.now().minusMonths(CustomerConstants.PLATINUM_MONTHS_LIMIT))) {
            return CustomerConstants.TIER_PLATINUM;
        }

        if (c.getAnnualSpend().compareTo(CustomerConstants.GOLD_THRESHOLD) >= 0 &&
                c.getLastPurchaseDate() != null &&
                c.getLastPurchaseDate().isAfter(LocalDateTime.now().minusMonths(CustomerConstants.GOLD_MONTHS_LIMIT))) {
            return CustomerConstants.TIER_GOLD;
        }

        return CustomerConstants.TIER_SILVER;
    }
}
