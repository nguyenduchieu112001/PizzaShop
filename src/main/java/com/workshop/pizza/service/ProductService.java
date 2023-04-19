package com.workshop.pizza.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.pizza.controller.form.AddProductRequest;
import com.workshop.pizza.controller.form.ProductRequest;
import com.workshop.pizza.controller.form.UUIDGenerator;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.dto.ProductDto;
import com.workshop.pizza.dto.mapper.ProductDtoMapper;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.entity.ProductType;
import com.workshop.pizza.entity.Size;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IProductRepository;
import com.workshop.pizza.repository.IProductSizeRepository;
import com.workshop.pizza.repository.IProductTypeRepository;
import com.workshop.pizza.repository.ISizeRepository;

@Service
public class ProductService {

	@Autowired
	private IProductRepository productRepository;

	@Autowired
	private IProductTypeRepository productTypeRepository;

	@Autowired
	private IProductSizeRepository productSizeRepository;

	@Autowired
	private ISizeRepository sizeRepository;

	@Autowired
	private ProductDtoMapper productDtoMapper;
	
	public double setPrice(Product product, Size size) {
	    return Math.round((product.getPrice() + product.getPrice() * size.getPercentPrice()) / 1000) * 1000;
	}

	public void saveProductSize(int productId, int sizeId) {
		ProductSize productSize = new ProductSize();
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new NotFoundException("Product doesn't exist"));
		Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new NotFoundException("Size doesn't exist"));
		productSize.setProduct(product);
		productSize.setSize(size);
		productSize.setProductPrice(setPrice(product, size));
		productSizeRepository.save(productSize);
	}

	public Product save(AddProductRequest t) {
		if (containsNonNumericCharacters(t.getPrice()))
			throw new BadRequestException("Price must be numberic");
		Product product = new Product();
		ProductType productType = productTypeRepository.findById(t.getProductType().getId())
				.orElseThrow(() -> new NotFoundException("Product Type doesn't exist"));
		product.setProductName(t.getProductName());
		product.setPrice(t.getPrice());
		product.setDescription(t.getDescription());
		product.setProductType(productType);
		Product newProduct = productRepository.save(product);

		for (Size size : t.getSizes()) {
			saveProductSize(newProduct.getId(), size.getId());
		}

		return newProduct;
	}

	public boolean containsNonNumericCharacters(double price) {
		String priceString = Double.toString(price);
		return !priceString.matches("[0-9.]+");
	}

	public void uploadImage(MultipartFile file, int id) throws IOException {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product is not found"));
		if (product.getImage() != null) {
			Path imagePath = Paths.get("src/main/resources/static" + product.getImage());
			ImageService.deleteImage(imagePath);
			String image = ImageService.uploadImage(file, UUIDGenerator.generate());
			product.setImage("/images/" + image);
			productRepository.save(product);
		} else {
			String image = ImageService.uploadImage(file, UUIDGenerator.generate());
			product.setImage("/images/" + image);
			productRepository.save(product);
		}

	}

	public void updateProductSizePrice(int productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new NotFoundException("Product doesn't exist"));
		Set<ProductSize> productSizes = productSizeRepository.findByProductId(productId);
		for (ProductSize productSize : productSizes) {
			productSize
					.setProductPrice(setPrice(product, productSize.getSize()));
			productSizeRepository.save(productSize);
		}
	}

//	public boolean existProductSize(int productId, int sizeId) {
//		Product product = productRepository.findById(productId)
//				.orElseThrow(() -> new NotFoundException("Product doesn't exist"));
//		Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new NotFoundException("Size doesn't exist"));
//		return productSizeRepository.existsByProductAndSize(product, size);
//	}

	public Product updateProduct(ProductRequest product, int id) {

		if (containsNonNumericCharacters(product.getPrice()))
			throw new BadRequestException("Price must be numberic");
		else {
			Product existingProduct = productRepository.findById(id)
					.orElseThrow(() -> new NotFoundException("Product doesn't exist with id:" + id));
			if (product.getProductName() != null)
				existingProduct.setProductName(product.getProductName());
			if (product.getPrice() != 0) {
				existingProduct.setPrice(product.getPrice());
				updateProductSizePrice(id);
			}
			if (product.getDescription() != null)
				existingProduct.setDescription(product.getDescription());
			if (product.getProductType() != null)
				existingProduct.setProductType(product.getProductType());
			if (product.getSizes() != null) {
				deleteProductSize(id);
				for (Size size : product.getSizes()) {
					saveProductSize(id, size.getId());
				}
			}

			return productRepository.save(existingProduct);
		}
	}

	public ProductDto getProduct(int id) {
		return productRepository.findById(id).map(productDtoMapper)
				.orElseThrow(() -> new NotFoundException("Product doesn't found with id: " + id));

	}
	
	public PageDto<ProductDto> getAllProducts(int page, int size, String query) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "productName"));
		Page<Product> productPage = productRepository.findByProductNameContainingAndDeletedAtIsNull(query, pageable);
		List<ProductDto> productDtos = productPage.getContent().stream().map(productDtoMapper)
				.collect(Collectors.toList());
		long totalElements = productPage.getTotalElements();
		int totalPages = productPage.getTotalPages();
		return new PageDto<>(productDtos, totalElements, totalPages);
	}
	
//	public PageDto<ProductSizeDto> getAllProducts(int page, int size, String query) {
//		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "product.productName"));
//		Page<ProductSize> productSizePage = productSizeRepository.findByProductProductNameContainingAndProductDeletedAtIsNull(pageable, query);
//		List<ProductSizeDto> productSizeDtos = productSizePage.getContent().stream().map(productSizeDtoMapper).collect(Collectors.toList());
//		long totalElements = productSizePage.getTotalElements();
//		int totalPages = productSizePage.getTotalPages();
//		return new PageDto<>(productSizeDtos, totalElements, totalPages);
//	}
	
	public void deleteProductSize(int productId) {
		Set<ProductSize> productSizes = productSizeRepository.findByProductId(productId);
		for(ProductSize productSize : productSizes) {
			productSizeRepository.deleteById(productSize.getId());
		}
	}

	public void deleteProduct(int id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product doesn't exists"));
		product.setDeletedAt(LocalDate.now());
		deleteProductSize(id);
		productRepository.save(product);
	}

	public Optional<Product> findByProductName(String name) {
		return productRepository.findByProductNameAndDeletedAtIsNull(name);
	}

	public List<ProductDto> findAllDistinctProducts() {
		return productRepository.findAll().stream().map(productDtoMapper).collect(Collectors.toList());
	}
}
