package com.workshop.pizza.controller.form;

import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

	@NotNull
	private String recipient;
	@NotNull
	private String messageBody;
	@NotNull
	private String subject;
	private String attachment;
	
	private String code;
	private LocalTime expirationTime;
}
