package com.workshop.pizza.dto.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.workshop.pizza.dto.ProductDto;
import com.workshop.pizza.dto.ProductSizeDto;
import com.workshop.pizza.dto.ProductTypeDto;
import com.workshop.pizza.dto.SizeDto;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.entity.Size;
import com.workshop.pizza.repository.IProductRepository;
import com.workshop.pizza.repository.ISizeRepository;

@Service
public class ProductSizeDtoMapper implements Function<ProductSize, ProductSizeDto> {

	@Autowired
	private IProductRepository productRepository;

	@Autowired
	private ISizeRepository sizeRepository;

	@Override
	public ProductSizeDto apply(ProductSize t) {
		Product product = productRepository.findById(t.getProduct().getId()).orElseThrow();
		Size size = sizeRepository.findById(t.getSize().getId()).orElseThrow();
		ProductTypeDto productTypeDto = new ProductTypeDto(product.getProductType().getId(),
				product.getProductType().getName());
		ProductDto productDto = new ProductDto(product.getId(), product.getProductName(), product.getPrice(),
				product.getDescription(), product.getImage(), product.getCreatedAt(), product.getUpdatedAt(),
				productTypeDto);
		SizeDto sizeDto = new SizeDto(size.getId(), size.getName(), size.getPercentPrice());
		return new ProductSizeDto(t.getId(), productDto, sizeDto, t.getProductPrice());
	}
}
