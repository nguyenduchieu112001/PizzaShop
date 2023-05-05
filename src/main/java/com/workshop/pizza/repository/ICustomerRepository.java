package com.workshop.pizza.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.workshop.pizza.entity.Customer;

public interface ICustomerRepository extends JpaRepository<Customer, Integer> {

	Optional<Customer> findByEmail(String email);

	Optional<Customer> findByUsername(String username);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	boolean existsByPhoneNumber(String phoneNumber);

	Page<Customer> findByCustomerNameContainingOrUsernameContainingOrEmailContainingOrPhoneNumberContaining(Pageable pageable, String query1, String query2, String query3, String query4);
}
