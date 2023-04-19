package com.workshop.pizza.controller.form;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class CustomerRequest {

	@Length(min = 10, message = "Customer name must at least 10 characters")
	private String customerName;
	
	@Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "Invalid phone number. Phone number must start with '+84' or '0' and must be 10 digits long.")
	@Column(unique = true)
	private String phoneNumber;
	
	@Length(min = 10)
	private String address;
	
	@Email
	private String email;
}
