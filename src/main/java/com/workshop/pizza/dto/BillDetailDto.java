package com.workshop.pizza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillDetailDto {

	private int id;
	private int quantity;
	private ProductSizeDto productSize;

}
