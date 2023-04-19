package com.workshop.pizza.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductType;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IProductRepository;
import com.workshop.pizza.repository.IProductTypeRepository;

@Service
public class ProductTypeService implements ICommonService<ProductType> {

	@Autowired
	private IProductTypeRepository productTypeRepository;

	@Autowired
	private IProductRepository productRepository;

	@Override
	public List<ProductType> getAll() {
		return productTypeRepository.findAll();
	}

	// get Product Type by id with product relationship
	public ProductType getProductTypeById(int id) {
		ProductType productType = productTypeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product Type doesn't exist with id: " + id));
		ProductType newproductType = new ProductType();
		newproductType.setId(productType.getId());
		newproductType.setName(productType.getName());
		List<Product> listProducts = productRepository.findByProductTypeId(id);
		Set<Product> list = new HashSet<>();
		for (Product product : listProducts) {
			Product productDto = new Product();
			productDto.setId(product.getId());
			productDto.setProductName(product.getProductName());
			productDto.setPrice(product.getPrice());
			productDto.setDescription(product.getDescription());
			productDto.setImage(product.getImage());
			productDto.setCreatedAt(product.getCreatedAt());
			productDto.setUpdatedAt(product.getUpdatedAt());
			list.add(productDto);
		}
		newproductType.setProducts(list);
		return newproductType;
	}

	// get all Product Type with Product relationship
	public List<ProductType> getProductTypes() {
		// Tạo mới 1 danh sách ProductType
		List<ProductType> listProductType = new ArrayList<>();
		// Lấy hết tất cả productType từ database bỏ vào productTypes
		List<ProductType> productTypes = productTypeRepository.findAll(Sort.by(Sort.Direction.DESC, "name"));
		// duyệt từng phần tử trong danh sách productType
		for (ProductType productType : productTypes) {
			// Tạo mới một productType
			ProductType newProductType = new ProductType();
			newProductType.setId(productType.getId());
			newProductType.setName(productType.getName());
			// Lấy hết danh sách product có liên quan tới productType có cùng id bỏ vào
			// listProducts
			List<Product> listProducts = productRepository.findByProductTypeId(productType.getId());
			// tạo mới danh sách listProduc
			Set<Product> listProduct = new HashSet<>();
			// Duyệt từng phần từ trong danh sách listProducts
			for (Product product : listProducts) {
				// Tạo mới một produc
				Product productDto = new Product();
				productDto.setId(product.getId());
				productDto.setProductName(product.getProductName());
				productDto.setPrice(product.getPrice());
				productDto.setDescription(product.getDescription());
				productDto.setImage(product.getImage());
				productDto.setCreatedAt(product.getCreatedAt());
				productDto.setUpdatedAt(product.getUpdatedAt());
				// thêm phần tử product vào danh sách listProduct
				listProduct.add(productDto);
			}
			newProductType.setProducts(listProduct);
			// Lưu newProductType vào listProductType
			listProductType.add(newProductType);
		}

		return listProductType;
	}

	@Override
	public ProductType save(ProductType t) {
		return productTypeRepository.save(t);
	}

	@Override
	public ProductType update(ProductType t, int id) {
		ProductType existProductType = productTypeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product Type doesn't exist with id: " + id));
		existProductType.setName(t.getName());
		return productTypeRepository.save(existProductType);
	}

	@Override
	public Optional<ProductType> getById(int id) {
		return productTypeRepository.findById(id);
	}

	@Override
	public void delete(int id) {
		productTypeRepository.deleteById(id);
	}

	public Optional<ProductType> findByProductTypeName(String name) {
		return productTypeRepository.findByName(name);
	}

	public ProductType findByName(String name) {
		Optional<ProductType> productType = productTypeRepository.findByName(name);
		ProductType newproductType = new ProductType();
		newproductType.setId(productType.get().getId());
		newproductType.setName(productType.get().getName());
		List<Product> listProducts = productRepository.findByProductTypeId(productType.get().getId());
		Set<Product> list = new HashSet<>();
		for (Product product : listProducts) {
			Product productDto = new Product();
			productDto.setId(product.getId());
			productDto.setProductName(product.getProductName());
			productDto.setPrice(product.getPrice());
			productDto.setDescription(product.getDescription());
			productDto.setImage(product.getImage());
			productDto.setCreatedAt(product.getCreatedAt());
			productDto.setUpdatedAt(product.getUpdatedAt());
			list.add(productDto);
		}
		newproductType.setProducts(list);
		return newproductType;
	}
	
	public PageDto<ProductType> getProductTypes(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Page<ProductType> productTypePage = productTypeRepository.findAll(pageable);
		List<ProductType> productType = productTypePage.getContent();
		long totalElements = productTypePage.getTotalElements();
		int totalPages = productTypePage.getTotalPages();
		return new PageDto<>(productType, totalElements, totalPages);
	}
	
	//Phân trang và tìm kiếm
	public PageDto<ProductType> getAllProductTypes(int page, int size, String query) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Page<ProductType> productTypePage = productTypeRepository.findByNameContaining(pageable, query);
		List<ProductType> productType = productTypePage.getContent();
		long totalElements = productTypePage.getTotalElements();
		int totalPages = productTypePage.getTotalPages();
		return new PageDto<>(productType, totalElements, totalPages);
	}
}
