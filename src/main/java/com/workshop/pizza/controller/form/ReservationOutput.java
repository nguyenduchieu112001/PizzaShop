package com.workshop.pizza.controller.form;

import java.time.LocalDate;
import java.time.LocalTime;

import com.workshop.pizza.dto.CustomerDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationOutput {

	private int id;
	private String reservationCode;
	private LocalDate createdAt;
	private LocalDate reservationDate;
	private LocalTime reservationTime;
	private int partySize;
	private String reservationStatus;
	private CustomerDto customer;
}
