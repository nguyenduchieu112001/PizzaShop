package com.workshop.pizza.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate reservationDate;
	
	@NotNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime reservationTime;
	
	@NotNull
	@Min(value = 2, message = "Party size should be at least 2")
	@Max(value = 6, message = "Party size should be at most 6")
	private int partySize;
	
	@NotNull
	private CustomerDto customer;
}
