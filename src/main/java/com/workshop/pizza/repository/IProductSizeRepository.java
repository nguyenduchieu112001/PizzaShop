package com.workshop.pizza.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.entity.Size;

public interface IProductSizeRepository extends JpaRepository<ProductSize, Integer> {

	ProductSize findByProductAndSize(Product product, Size size);

	Set<ProductSize> findByProductId(int productId);
	
	boolean existsByProductAndSize(Product product, Size size);
	
	boolean existsBySize(Size size);

	Page<ProductSize> findByProductProductNameContainingAndProductDeletedAtIsNull(Pageable pageable, String query);

	List<Size> findSizesByProductId(int id);

	

}
