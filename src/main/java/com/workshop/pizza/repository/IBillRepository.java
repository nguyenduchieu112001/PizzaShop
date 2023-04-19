package com.workshop.pizza.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.workshop.pizza.entity.Bill;

public interface IBillRepository extends JpaRepository<Bill, Integer>{

	Page<Bill> findByCustomerId(int customerID, Pageable pageable);

	Page<Bill> findByBillCodeContaining(String query, Pageable pageable);
}
