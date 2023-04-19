package com.workshop.pizza.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.workshop.pizza.validation.ValidateReservationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private String reservationCode;

	@CreatedDate
	private LocalDate createdAt;

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
	
	@ValidateReservationStatus
	private String reservationStatus;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	@JsonIgnore
	private Customer customer;
}
