package com.workshop.pizza.controller.form;

import java.util.Set;

import com.workshop.pizza.dto.CustomerDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillRequest {

	@NotNull(message = "Customer must not be null")
	private CustomerDto customer;

	@NotNull(message = "Bill Detail must not be null")
	private Set<BillDetailRequest> billDetails;

}
