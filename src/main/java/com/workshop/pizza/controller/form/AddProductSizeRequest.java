package com.workshop.pizza.controller.form;

import java.util.List;

import com.workshop.pizza.dto.ProductDto;
import com.workshop.pizza.dto.SizeDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddProductSizeRequest {

	private ProductDto product;
	private List<SizeDto> sizes;
}
