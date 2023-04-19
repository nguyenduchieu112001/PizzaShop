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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.entity.ProductType;
import com.workshop.pizza.exception.ConflictException;
import com.workshop.pizza.exception.MethodNotAllowedException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.service.ProductTypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/product-type")
public class ProductTypeController {

	@Autowired
	private ProductTypeService productTypeService;

	@GetMapping("/page")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageDto<ProductType>> getAllProductTypes(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "") String query) {
		if("".equals(query))
			return ResponseEntity.ok(productTypeService.getProductTypes(page-1, size));
		else
			return ResponseEntity.ok(productTypeService.getAllProductTypes(page-1, size, query));
	}
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<List<ProductType>> getProductTypes() {
		return ResponseEntity.ok(productTypeService.getProductTypes());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ProductType getProductType(@PathVariable int id) {
		if (productTypeService.getById(id).isEmpty())
			throw new NotFoundException("Product Type doesn't found with id: " + id);
		else
			return productTypeService.getProductTypeById(id);
	}

	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductType> addProductType(@Valid @RequestBody ProductType productType) {
		if (productTypeService.findByProductTypeName(productType.getName()).isPresent()) {
			throw new ConflictException("Product Type name already exists");
		} else
			return ResponseEntity.ok(productTypeService.save(productType));
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductType> updateProductType(@RequestBody ProductType productType, @PathVariable int id) {
		if (productTypeService.getById(id).isEmpty())
			throw new NotFoundException("Product Type doesn't found with id: " + id);
		if (productTypeService.findByProductTypeName(productType.getName()).isPresent())
			throw new ConflictException("Product Type name already exists");
		else
			return ResponseEntity.ok(productTypeService.update(productType, id));
	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ProductType deleteProductType(@PathVariable int id) {
		ProductType productType = productTypeService.getProductTypeById(id);
		if (productType == null)
			throw new NotFoundException("Product Type doesn't found with id: " + id);
		else if (productType.getProducts().isEmpty()) {
			productTypeService.delete(id);
			throw new OkException("Delete completed with id: " + id);
		} else
			throw new MethodNotAllowedException(
					"You cannot delete this Product Type because there are Products with values associated with it. ");
	}
}
