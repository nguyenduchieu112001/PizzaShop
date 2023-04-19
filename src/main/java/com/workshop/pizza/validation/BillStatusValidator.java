package com.workshop.pizza.validation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BillStatusValidator implements ConstraintValidator<ValidateBillStatus, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		List<String> billStatus = Arrays.asList("Processing", "Deposited", "Shipping", "Paid", "Canceled");
		return billStatus.contains(value);
	}

}
