package com.workshop.pizza.exception;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(value = { BadRequestException.class })
	public ResponseEntity<Object> handleApiRequestException(BadRequestException ex) {
		// 1. Create payload containing exception details
		HttpStatus badRequest = HttpStatus.BAD_REQUEST;
		ApiException apiException = new ApiException(ex.getMessage(), badRequest, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, badRequest);
	}

	@ExceptionHandler(value = { OkException.class })
	public ResponseEntity<Object> handleApiRequestException(OkException e) {
		HttpStatus ok = HttpStatus.OK;
		ApiException apiException = new ApiException(e.getMessage(), ok, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, ok);
	}

	@ExceptionHandler(value = { NotFoundException.class })
	public ResponseEntity<Object> handleApiRequestException(NotFoundException e) {
		HttpStatus notFound = HttpStatus.NOT_FOUND;
		ApiException apiException = new ApiException(e.getMessage(), notFound, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, notFound);
	}

	@ExceptionHandler(value = { UnauthorizedException.class })
	public ResponseEntity<Object> handleApiRequestException(UnauthorizedException e) {
		HttpStatus unauth = HttpStatus.UNAUTHORIZED;
		ApiException apiException = new ApiException(e.getMessage(), unauth, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, unauth);
	}

	@ExceptionHandler(value = ForbiddenException.class)
	public ResponseEntity<Object> handleApiRequestException(ForbiddenException e) {
		HttpStatus forbidden = HttpStatus.FORBIDDEN;
		ApiException apiException = new ApiException(e.getMessage(), forbidden, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, forbidden);
	}

	// change @Valid to exception
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleApiValidExArgument(MethodArgumentNotValidException ex) {

		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
		ApiException apiException = new ApiException(errors.toString(), HttpStatus.BAD_REQUEST,
				ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = ConstraintViolationException.class)
	public ResponseEntity<Object> handleApiValidExArgument(ConstraintViolationException ex) {
		List<String> errors = new ArrayList<>();
		ex.getConstraintViolations().forEach((violation) -> {
			String errorMessage = violation.getMessage();
			errors.add(errorMessage);
		});

		ApiException apiException = new ApiException(errors.toString(), HttpStatus.BAD_REQUEST,
				ZonedDateTime.now(ZoneId.of("Z")));
		return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = CreatedException.class)
	public ResponseEntity<Object> handleApiRequestException(CreatedException e) {
		HttpStatus created = HttpStatus.CREATED;
		ApiException apiException = new ApiException(e.getMessage(), created, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, created);
	}

	@ExceptionHandler(value = ConflictException.class)
	public ResponseEntity<Object> handleApiRequestException(ConflictException e) {
		HttpStatus conflict = HttpStatus.CONFLICT;
		ApiException apiException = new ApiException(e.getMessage(), conflict, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, conflict);
	}

	@ExceptionHandler(value = MethodNotAllowedException.class)
	public ResponseEntity<Object> handleApiRequestException(MethodNotAllowedException e) {
		HttpStatus methodNotAllowed = HttpStatus.METHOD_NOT_ALLOWED;
		ApiException apiException = new ApiException(e.getMessage(), methodNotAllowed,
				ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, methodNotAllowed);
	}

	@ExceptionHandler(value = BadGatewayException.class)
	public ResponseEntity<Object> handleApiRequestException(BadGatewayException e) {
		HttpStatus badGateway = HttpStatus.BAD_GATEWAY;
		ApiException apiException = new ApiException(e.getMessage(), badGateway, ZonedDateTime.now(ZoneId.of("Z")));

		// 2 . Return respone entity
		return new ResponseEntity<>(apiException, badGateway);
	}
	
	
}
