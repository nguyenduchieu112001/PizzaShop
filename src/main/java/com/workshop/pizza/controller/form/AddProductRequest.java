package com.workshop.pizza.controller.form;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.workshop.pizza.entity.ProductType;
import com.workshop.pizza.entity.Size;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductRequest {

	@NotNull(message = "Product name shouldn't be null")
	@Length(min = 5, max = 250, message = "Product name must have 5-250 characters")
	private String productName;
	
	@NotNull(message = "Price must not be null")
	@DecimalMin(value = "0", message = "Price must be atleast 0.00")
	@DecimalMax(value = "1000000", message = "Price should not be greater than 1000000")
	private double price;
	
	@NotNull(message = "Description must not be null")
	@Length(min = 5, message = "Description has at least 5 characters")
	private String description;
	
	@NotNull(message = "Product must have product type")
	private ProductType productType;
	
	@NotNull(message = "Product must have at least 1 size")
	private List<Size> sizes;
	
}
