package com.workshop.pizza.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.entity.Size;
import com.workshop.pizza.exception.MethodNotAllowedException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.repository.IProductSizeRepository;
import com.workshop.pizza.repository.ISizeRepository;

@Service
public class SizeService {

	@Autowired
	private ISizeRepository sizeRepository;

	@Autowired
	private IProductSizeRepository productSizeRepository;

	public List<Size> listSizes() {
		return sizeRepository.findAll();
	}

	public Size getSize(int id) {
		return sizeRepository.findById(id).orElseThrow(() -> new NotFoundException("Product Size doesn't exists"));
	}

	public Size save(Size size) {
		return sizeRepository.save(size);
	}

	public Size update(Size size, int id) {
		Size existSize = sizeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product Size doesn't exists"));
		if (size.getName() != null)
			existSize.setName(size.getName());
		if (size.getPercentPrice() != 0)
			existSize.setPercentPrice(size.getPercentPrice());
		return sizeRepository.save(existSize);
	}

	public void delete(int id) {
		Size size = sizeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Product Size doesn't found with id: " + id));
		if (productSizeRepository.existsBySize(size)) {
			throw new MethodNotAllowedException(
					"You cannot delete this Product Size because there are Products with values associated with it. ");
		}
		else {
			sizeRepository.deleteById(id);
			throw new OkException("Delete completed with id: " + id);
		}
	}

	public Size findByName(String name) {
		return sizeRepository.findByName(name);
	}
	
	public PageDto<Size> getProductSizes(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Page<Size> productSizePage = sizeRepository.findAll(pageable);
		List<Size> productType = productSizePage.getContent();
		long totalElements = productSizePage.getTotalElements();
		int totalPages = productSizePage.getTotalPages();
		return new PageDto<>(productType, totalElements, totalPages);
	}
	
	public PageDto<Size> getAllProductSizes(int page, int size, String query) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
		Page<Size> productSizePage = sizeRepository.findByNameContaining(pageable, query);
		List<Size> productType = productSizePage.getContent();
		long totalElements = productSizePage.getTotalElements();
		int totalPages = productSizePage.getTotalPages();
		return new PageDto<>(productType, totalElements, totalPages);
	}

}
