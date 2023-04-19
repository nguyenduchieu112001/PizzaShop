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
@Constraint(validatedBy = BillStatusValidator.class)
public @interface ValidateBillStatus {

	public String message() default "Invalid Bill Status: It should be either Processing, Deposited, Shipping, Paid, Canceled";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
