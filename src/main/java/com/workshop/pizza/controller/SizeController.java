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
import com.workshop.pizza.entity.Size;
import com.workshop.pizza.exception.ConflictException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.service.SizeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/size")
public class SizeController {

	@Autowired
	private SizeService sizeService;
	
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<List<Size>> listSizes() {
		return ResponseEntity.ok(sizeService.listSizes());
	}
	
	@GetMapping("/page")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageDto<Size>> getAllProductTypes(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "") String query) {
		if("".equals(query))
			return ResponseEntity.ok(sizeService.getProductSizes(page-1, size));
		else
			return ResponseEntity.ok(sizeService.getAllProductSizes(page-1, size, query));
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<Size> getSize(@PathVariable int id) {
		return ResponseEntity.ok(sizeService.getSize(id));
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Size> addProductSize(@Valid @RequestBody Size size) {
		if(sizeService.findByName(size.getName()) != null) {
			throw new ConflictException("Product Size name already exists");
		} else return ResponseEntity.ok(sizeService.save(size));
	}
	
	@PutMapping("/update/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Size> updateProductSize(@RequestBody Size size, @PathVariable int id) {
		if (sizeService.getSize(id) == null) {
			throw new NotFoundException("Product Size doesn't found with id: " + id);
		}
		if (sizeService.findByName(size.getName()) != null) {
			throw new ConflictException("Product Size name already exists");
		}
		else return ResponseEntity.ok(sizeService.update(size, id));
	}
	
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Size> deleteProductSize(@PathVariable int id) {
		sizeService.delete(id);
		return ResponseEntity.ok().build();
	}
	
}
