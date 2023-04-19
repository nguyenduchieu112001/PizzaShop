package com.workshop.pizza.validation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ReservationStatusValidator implements ConstraintValidator<ValidateReservationStatus, String>{

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		List<String> reservationStatus = Arrays.asList("Deposited", "Canceled", "Paid");
		return reservationStatus.contains(value);
	}

}
