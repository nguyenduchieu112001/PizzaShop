package com.workshop.pizza.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.braintreepayments.http.HttpResponse;
import com.paypal.orders.Order;
import com.workshop.pizza.controller.form.ReservationOutput;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.dto.ReservationDto;
import com.workshop.pizza.entity.Reservation;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.service.PayPalService;
import com.workshop.pizza.service.ReservationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/reservation")
public class ReservationController {

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private PayPalService payPalService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageDto<ReservationOutput>> getReservationBySearch(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "25") int size, @RequestParam(defaultValue = "") String query) {
		if("".equals(query))
			return ResponseEntity.ok(reservationService.getAllReservations(page-1, size));
		else
			return ResponseEntity.ok(reservationService.getReservationBySearch(page-1, size, query));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<ReservationOutput> getReservation(@PathVariable int id) {
		return ResponseEntity.ok(reservationService.getReservation(id));
	}

	@PostMapping("/check-reservation")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> checkReservation(@RequestBody ReservationDto reservation) {
		Boolean check = reservationService.checkReservationDateTime(reservation);
		if (Boolean.TRUE.equals(check)) {
			throw new OkException("You can make a reservation");
		} else {
			throw new BadRequestException("You can't make a reservation");
		}
	}

	@PostMapping("/create-order")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> createOrder(@Valid @RequestBody ReservationDto reservation) {
		try {
			HttpResponse<Order> response = payPalService.createOrder(reservation);
			String orderId = response.result().id();
			return ResponseEntity.ok(orderId);
		} catch (IOException e) {
			throw new BadRequestException("Error creating order");
		}
	}

	@PostMapping("/book-table")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<ReservationOutput> getAuthorization(@Valid @RequestBody ReservationDto reservation) {
		Reservation newReservation = reservationService.bookTable(reservation);
		return ResponseEntity.ok(reservationService.getReservation(newReservation.getId()));
	}

	@PutMapping("/change-status-canceled/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Optional<Reservation>> changeCanceledStatus(@PathVariable int id) {
		reservationService.delete(id);
		return ResponseEntity.ok(reservationService.getById(id));
	}

	@PutMapping("/change-status-paid/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Optional<Reservation>> changePaidStatus(@PathVariable int id) {
		reservationService.changePaidStatus(id);
		return ResponseEntity.ok(reservationService.getById(id));
	}
}
