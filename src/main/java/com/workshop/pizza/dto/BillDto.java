package com.workshop.pizza.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class BillDto {

	private int id;
	private String billCode;
	private double total;
	private String billStatus;
	private LocalDate createdAt;
	private CustomerDto customer;
	private Set<BillDetailDto> orders;

}
