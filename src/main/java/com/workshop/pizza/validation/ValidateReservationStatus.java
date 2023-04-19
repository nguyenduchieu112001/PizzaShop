package com.workshop.pizza.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ReservationStatusValidator.class)
public @interface ValidateReservationStatus {

	public String message() default "Invalid Reservation Status: It should be either Deposited, Canceled, Paid";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
