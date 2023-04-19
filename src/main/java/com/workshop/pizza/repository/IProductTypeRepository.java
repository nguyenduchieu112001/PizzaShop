package com.workshop.pizza.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.workshop.pizza.entity.ProductType;

public interface IProductTypeRepository extends JpaRepository<ProductType, Integer>{

	Optional<ProductType> findByName(String name);

	Page<ProductType> findByNameContaining(Pageable pageable, String query);
}
