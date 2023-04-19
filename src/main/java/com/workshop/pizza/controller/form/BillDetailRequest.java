package com.workshop.pizza.controller.form;

import com.workshop.pizza.dto.ProductDto;
import com.workshop.pizza.dto.SizeDto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDetailRequest {

	@NotNull(message = "Quantity must not be null")
	@DecimalMin(value = "1", message = "Quantity must be atleast 1")
	@DecimalMax(value = "10", message = "Quantity should not be greater than 10")
	private int quantity;
	
	@NotNull(message = "Product must not be null")
	private ProductDto product;
	
	@NotNull(message = "Size must not be null")
	private SizeDto size;
}
