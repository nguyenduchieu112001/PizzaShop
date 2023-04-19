package com.workshop.pizza.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.workshop.pizza.entity.Admin;

public interface IAdminRepository extends JpaRepository<Admin, Integer>{

	Optional<Admin> findByUsername(String name);
}
