package com.workshop.pizza.dto.mapper;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.pizza.dto.CustomerDto;
import com.workshop.pizza.dto.ReservationDto;
import com.workshop.pizza.entity.Customer;
import com.workshop.pizza.entity.Reservation;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.ICustomerRepository;

@Service
public class ReservationDtoMapper implements Function<Reservation, ReservationDto> {

	@Autowired
	private ICustomerRepository customerRepository;

	@Override
	public ReservationDto apply(Reservation t) {

		Customer customer = customerRepository.findById(t.getCustomer().getId())
				.orElseThrow(() -> new NotFoundException("Customer doesn't exists"));
		CustomerDto newCustomer = new CustomerDto(customer.getId(), customer.getCustomerName(),
				customer.getEmail(), customer.getPhoneNumber(), customer.getAddress(), customer.getUsername());
		return new ReservationDto(t.getId(), t.getReservationCode(), t.getCreatedAt(), t.getReservationDate(),
				t.getReservationTime(), t.getPartySize(), t.getReservationStatus(), newCustomer);
	}

}
