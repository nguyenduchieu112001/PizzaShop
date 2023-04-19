package com.workshop.pizza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeDto {

	private int id;
	private String name;
	private double percentPrice;
}
