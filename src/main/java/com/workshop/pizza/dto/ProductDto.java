package com.workshop.pizza.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {

	private int id;
	private String productName;
	private double price;
	private String description;
	private String image;
	private LocalDate createdAt;
	private LocalDate updatedAt;
	private ProductTypeDto productType;
}
