package com.workshop.pizza.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.entity.Size;

public interface IProductSizeRepository extends JpaRepository<ProductSize, Integer> {

	ProductSize findByProductAndSize(Product product, Size size);

	Set<ProductSize> findByProductIdAndDeletedAtIsNull(int productId);
	
	boolean existsByProductAndSizeAndDeletedAtIsNull(Product product, Size size);
	
	boolean existsBySize(Size size);

	Page<ProductSize> findByProductProductNameContainingAndProductDeletedAtIsNull(Pageable pageable, String query);

	List<Size> findSizesByProductId(int id);
	
	@Query("SELECT ps FROM ProductSize ps WHERE ps.deletedAt IS NULL GROUP BY ps.product.id")
	List<ProductSize> findByDeletedAtIsNullGroupByProductId();

	List<ProductSize> findBySize(Size size);

}
