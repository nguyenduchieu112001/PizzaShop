package com.workshop.pizza.dto.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.workshop.pizza.dto.CustomerDto;
import com.workshop.pizza.entity.Customer;

@Service
public class CustomerDtoMapper implements Function<Customer, CustomerDto> {


	@Override
	public CustomerDto apply(Customer t) {
		return new CustomerDto(t.getId(), t.getCustomerName(), t.getEmail(), t.getPhoneNumber(), t.getAddress(),
				t.getUsername());
	}

}
