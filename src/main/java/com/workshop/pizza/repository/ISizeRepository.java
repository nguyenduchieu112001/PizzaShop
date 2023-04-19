package com.workshop.pizza.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.workshop.pizza.entity.Size;

public interface ISizeRepository extends JpaRepository<Size, Integer>{

	Size findByName(String name);

	Page<Size> findByNameContaining(Pageable pageable, String query);
}
