package com.workshop.pizza.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

	private int id;
	private String reservationCode;
	private LocalDate createdAt;
	private LocalDate reservationDate;
	private LocalTime reservationTime;
	private int partySize;
	private String reservationStatus;
	private CustomerDto customer;
}
