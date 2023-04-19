package com.workshop.pizza.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.pizza.controller.form.AddProductSizeRequest;
import com.workshop.pizza.controller.form.UpdateProductSizeRequest;
import com.workshop.pizza.dto.ProductSizeDto;
import com.workshop.pizza.dto.SizeDto;
import com.workshop.pizza.dto.mapper.ProductSizeDtoMapper;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.entity.Size;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IProductRepository;
import com.workshop.pizza.repository.IProductSizeRepository;
import com.workshop.pizza.repository.ISizeRepository;

@Service
public class ProductSizeService {

	@Autowired
	private IProductSizeRepository productSizeRepository;

	@Autowired
	private IProductRepository productRepository;

	@Autowired
	private ISizeRepository sizeRepository;

	@Autowired
	private ProductSizeDtoMapper productSizeDtoMapper;

	public List<ProductSizeDto> getAllProductSizes() {
		return productSizeRepository.findAll().stream().map(productSizeDtoMapper).collect(Collectors.toList());
	}

	public ProductSize getProductSizeById(int id) {
		return productSizeRepository.findById(id).orElseThrow(() -> new NotFoundException("ProductSize doesn't found"));
	}
	
	public double setPrice(Product product, Size size) {
	    return Math.round((product.getPrice() + product.getPrice() * size.getPercentPrice()) / 1000) * 1000;
	}

	public void createProductSize(AddProductSizeRequest productSize) {
		Product product = productRepository.findById(productSize.getProduct().getId())
				.orElseThrow(() -> new NotFoundException("Product doesn't exists"));
		for (SizeDto sizeDto : productSize.getSizes()) {
			Size size = sizeRepository.findById(sizeDto.getId())
					.orElseThrow(() -> new NotFoundException("Size doesn't exists"));
			if(Boolean.FALSE.equals(productSizeRepository.existsByProductAndSize(product, size))) {
				ProductSize newProductSize = new ProductSize();
				newProductSize.setProduct(product);
				newProductSize.setSize(size);
				newProductSize.setProductPrice(setPrice(product, size));
				productSizeRepository.save(newProductSize);
			}
		}
	}

	public ProductSize updateProductSize(int id, UpdateProductSizeRequest productSizeRequest) {
		ProductSize productSize = productSizeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product Size doesn't found"));
		productSize.setProductPrice(productSizeRequest.getProductPrice());

		return productSizeRepository.save(productSize);
	}

	public void deleteProductSize(int id) {
		productSizeRepository.deleteById(id);
	}

	public List<ProductSizeDto> getProductSizeByProductId(int id) {

		productRepository.findById(id).orElseThrow(() -> new NotFoundException("Product doesn't found"));
		return productSizeRepository.findByProductId(id).stream().map(productSizeDtoMapper)
				.collect(Collectors.toList());
	}
}
