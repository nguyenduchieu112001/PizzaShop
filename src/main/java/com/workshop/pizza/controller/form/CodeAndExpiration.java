package com.workshop.pizza.controller.form;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeAndExpiration {

	private String code;
	private LocalTime expirationTime;
}
