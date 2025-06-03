// package com.tcs.assignment.customer.service;

// import com.tcs.assignment.customer.constant.CustomerConstants;
// import com.tcs.assignment.customer.dto.CustomerRequest;
// import com.tcs.assignment.customer.dto.CustomerResponse;
// import com.tcs.assignment.customer.entity.Customer;
// import com.tcs.assignment.customer.repository.CustomerRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Service;
// import org.springframework.web.server.ResponseStatusException;

// import java.time.LocalDateTime;
// import java.util.UUID;

// @Service
// @RequiredArgsConstructor
// public class CustomerService {

//     private final CustomerRepository repo;

//     public CustomerResponse create(CustomerRequest request) {
//         Customer customer = new Customer(null, request.getName(), request.getEmail(),
//                 request.getAnnualSpend(), request.getLastPurchaseDate());
//         return toResponse(repo.save(customer));
//     }

//     public CustomerResponse getById(UUID id) {
//         return repo.findById(id)
//                 .map(this::toResponse)
//                 .orElseThrow(() -> notFoundException());
//     }

//     public CustomerResponse getByName(String name) {
//         return repo.findByName(name)
//                 .map(this::toResponse)
//                 .orElseThrow(() -> notFoundException());
//     }

//     public CustomerResponse getByEmail(String email) {
//         return repo.findByEmail(email)
//                 .map(this::toResponse)
//                 .orElseThrow(() -> notFoundException());
//     }

//     public CustomerResponse update(UUID id, CustomerRequest request) {
//         Customer customer = repo.findById(id)
//                 .orElseThrow(() -> notFoundException());

//         customer.setName(request.getName());
//         customer.setEmail(request.getEmail());
//         customer.setAnnualSpend(request.getAnnualSpend());
//         customer.setLastPurchaseDate(request.getLastPurchaseDate());

//         return toResponse(repo.save(customer));
//     }

//     public void delete(UUID id) {
//         if (!repo.existsById(id)) throw notFoundException();
//         repo.deleteById(id);
//     }

//     private CustomerResponse toResponse(Customer customer) {
//         return new CustomerResponse(
//                 customer.getId(),
//                 customer.getName(),
//                 customer.getEmail(),
//                 customer.getAnnualSpend(),
//                 customer.getLastPurchaseDate(),
//                 calculateTier(customer)
//         );
//     }

//     private String calculateTier(Customer c) {
//         if (c.getAnnualSpend() == null) return CustomerConstants.TIER_SILVER;

//         if (c.getAnnualSpend().compareTo(CustomerConstants.PLATINUM_THRESHOLD) >= 0 &&
//                 c.getLastPurchaseDate() != null &&
//                 c.getLastPurchaseDate().isAfter(LocalDateTime.now().minusMonths(CustomerConstants.PLATINUM_MONTHS_LIMIT))) {
//             return CustomerConstants.TIER_PLATINUM;
//         }

//         if (c.getAnnualSpend().compareTo(CustomerConstants.GOLD_THRESHOLD) >= 0 &&
//                 c.getLastPurchaseDate() != null &&
//                 c.getLastPurchaseDate().isAfter(LocalDateTime.now().minusMonths(CustomerConstants.GOLD_MONTHS_LIMIT))) {
//             return CustomerConstants.TIER_GOLD;
//         }

//         return CustomerConstants.TIER_SILVER;
//     }

//     private ResponseStatusException notFoundException() {
//         return new ResponseStatusException(HttpStatus.NOT_FOUND, CustomerConstants.ERROR_CUSTOMER_NOT_FOUND);
//     }
// }

package com.tcs.assignment.customer.service;

import com.tcs.assignment.customer.dto.CustomerRequest;
import com.tcs.assignment.customer.dto.CustomerResponse;

import java.util.UUID;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);
    CustomerResponse getById(UUID id);
    CustomerResponse getByName(String name);
    CustomerResponse getByEmail(String email);
    CustomerResponse getByNameOrEmail(String name, String email);
    CustomerResponse update(UUID id, CustomerRequest request);
    void delete(UUID id);
}