package com.workshop.pizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workshop.pizza.entity.Customer;

public interface ICustomerRepository extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByEmail(String email);

	Optional<Customer> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);
}
