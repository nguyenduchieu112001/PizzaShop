package com.workshop.pizza.controller;

import java.io.IOException;

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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.pizza.controller.form.AddProductRequest;
import com.workshop.pizza.controller.form.ProductRequest;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.dto.ProductDto;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.service.ImageService;
import com.workshop.pizza.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageDto<ProductDto>> getProducts(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "") String query) {
		return ResponseEntity.ok(productService.getAllProducts(page - 1, size, query));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<ProductDto> getProduct(@PathVariable int id) {
		if (productService.getProduct(id) != null)
			return ResponseEntity.ok(productService.getProduct(id));
		else
			throw new NotFoundException("Product doesn't exist with id: " + id);
	}

	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Product> addProduct(@Valid @RequestBody AddProductRequest product) {
//		if (productService.findByProductName(product.getProductName()).isPresent())
//			throw new ConflictException("Product name already exists");
		return ResponseEntity.ok(productService.save(product));
	}

	@PostMapping("/{id}/image")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductDto> uploadImage(@PathVariable int id, @RequestPart("file") MultipartFile file)
			throws IOException {
		productService.uploadImage(file, id);
		return ResponseEntity.ok(productService.getProduct(id));
	}

	@GetMapping("/images/{file:.+}")
	@PreAuthorize("permitAll()")
	public Object download(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("file") String fileName) throws IOException {
		return ImageService.downloadImage(fileName);
	}

	@PutMapping("/update/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<ProductDto> updateProduct(@Valid @RequestBody ProductRequest productRequest,
			@PathVariable int id) {
		if (productService.getProduct(id) == null)
			throw new NotFoundException("Product doesn't found with id: " + id);
		else {
			productService.updateProduct(productRequest, id);
			return ResponseEntity.ok(productService.getProduct(id));
		}

	}

	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Product> deleteProduct(@PathVariable int id) {
		productService.deleteProduct(id);
		throw new OkException("Delete completed with id: " + id);
	}
}
