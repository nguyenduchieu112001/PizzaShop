package com.workshop.pizza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSizeDto {

	private int id;
	private ProductDto product;
	private SizeDto size;
	private double productPrice;
}
