package com.workshop.pizza.dto.mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.workshop.pizza.dto.ProductDto;
import com.workshop.pizza.dto.ProductTypeDto;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductType;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IProductTypeRepository;

@Service
public class ProductDtoMapper implements Function<Product, ProductDto> {

	@Autowired
	private IProductTypeRepository productTypeRepository;

	@Override
	public ProductDto apply(Product t) {
		ProductType productType = productTypeRepository.findById(t.getProductType().getId())
				.orElseThrow(() -> new NotFoundException("Product Type doesn't exists"));
		ProductTypeDto productTypeDto = new ProductTypeDto(productType.getId(), productType.getName());
			return new ProductDto(t.getId(), t.getProductName(), t.getPrice(), t.getDescription(), t.getImage(),
					t.getCreatedAt(), t.getUpdatedAt(), productTypeDto);

	}

}
