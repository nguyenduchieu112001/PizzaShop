package com.workshop.pizza.validation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductSizeNameValidator implements ConstraintValidator<ValidateProductSizeName, String> {

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		List<String> productSizeName = Arrays.asList("Regular", "Medium", "Large");
		return productSizeName.contains(value);
	}

}
