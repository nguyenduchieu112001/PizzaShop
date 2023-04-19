package com.workshop.pizza.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {

	private int id;
	private String customerName;
	private String email;
	private String phoneNumber;
	private String address;
	private String userName;
}
