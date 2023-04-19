package com.workshop.pizza.exception;

import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiException {

	private final String message;
	private final HttpStatus httpStatus;
	private final ZonedDateTime timeStamp;
}
