package com.workshop.pizza.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.workshop.pizza.entity.Product;

public interface IProductRepository extends JpaRepository<Product, Integer> {

	Optional<Product> findByProductNameAndDeletedAtIsNull(String name);

	List<Product> findByProductTypeId(int id);

	@Query(value = "SELECT pt.id FROM product as p INNER JOIN product_type as pt on pt.id = p.product_type_id WHERE p.id = ?1", nativeQuery = true)
	int findProductTypeByProductId(int productId);

	Page<Product> findByDeletedAtIsNull(Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p WHERE p.deletedAt IS NULL GROUP BY p.productName")
	List<Product> findAllDistinctProducts();

	Page<Product> findByProductNameContainingAndDeletedAtIsNull(String query, Pageable pageable);




}
