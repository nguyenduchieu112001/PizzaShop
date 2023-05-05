package com.workshop.pizza.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.pizza.controller.form.AddProductSizeRequest;
import com.workshop.pizza.controller.form.UpdateProductSizeRequest;
import com.workshop.pizza.dto.ProductSizeDto;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.service.ProductSizeService;

@RestController
@RequestMapping("/api/v1/product-size")
public class ProductSizeController {
	
	@Autowired
	private ProductSizeService productSizeService;

	@GetMapping("/product/{id}")
//	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<List<ProductSizeDto>> getProductSizeByProductId(@PathVariable int id) {
		return ResponseEntity.ok(productSizeService.getProductSizeByProductId(id));
	}
	
	@GetMapping("/menu")
	public ResponseEntity<List<ProductSizeDto>> findByDeletedAtIsNullGroupByProductId() {
		return ResponseEntity.ok(productSizeService.findByDeletedAtIsNullGroupByProductId());
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductSize> addProductSize(@RequestBody AddProductSizeRequest productSize) {
		productSizeService.createProductSize(productSize);
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductSize> updateProductSize(@RequestBody UpdateProductSizeRequest productPrice, @PathVariable int id) {
		productSizeService.updateProductSize(id, productPrice);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductSize> deleteProductSize(@PathVariable int id) {
		productSizeService.deleteProductSize(id);
		throw new OkException("Delete completed with id: " + id);
	}
	
}
