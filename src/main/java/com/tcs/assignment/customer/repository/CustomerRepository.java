package com.tcs.assignment.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tcs.assignment.customer.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByName(String name);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByNameAndEmail(String name, String email);
}
