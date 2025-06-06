package com.tcs.assignment.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.assignment.customer.entity.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findAllByNameIgnoreCase(String name);
    List<Customer> findAllByEmailIgnoreCase(String email);
    List<Customer> findAllByNameAndEmailAllIgnoreCase(String name, String email);
}
