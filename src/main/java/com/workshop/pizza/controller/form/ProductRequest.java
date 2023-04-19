package com.workshop.pizza.controller.form;


import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.workshop.pizza.entity.Size;
import com.workshop.pizza.entity.ProductType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

	@Length(min = 5, max = 250, message = "Product name must have 5-250 characters")
	private String productName;
	
	@DecimalMin(value = "0", message = "Price must be atleast 0.00")
	@DecimalMax(value = "1000000", message = "Price shhould not be greater than 1000000")
	private double price;
	
	@Length(min = 5, message = "Description has at least 5 characters")
	private String description;
	
	private ProductType productType;
	
	private List<Size> sizes;
}
