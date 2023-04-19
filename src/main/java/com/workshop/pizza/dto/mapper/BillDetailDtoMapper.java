package com.workshop.pizza.dto.mapper;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.pizza.dto.BillDetailDto;
import com.workshop.pizza.dto.ProductSizeDto;
import com.workshop.pizza.entity.BillDetail;

@Service
public class BillDetailDtoMapper implements Function<BillDetail, BillDetailDto> {
	
	@Autowired(required = true)
	private ProductSizeDtoMapper productSizeDtoMapper;

	@Override
	public BillDetailDto apply(BillDetail t) {
		ProductSizeDto productSizeDto = productSizeDtoMapper.apply(t.getProductSize());
		return new BillDetailDto(t.getId(), t.getQuantity(), productSizeDto);
	}

}