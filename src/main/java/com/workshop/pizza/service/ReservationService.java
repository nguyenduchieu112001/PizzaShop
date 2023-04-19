package com.workshop.pizza.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.workshop.pizza.controller.form.ReservationOutput;
import com.workshop.pizza.controller.form.UUIDGenerator;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.dto.ReservationDto;
import com.workshop.pizza.dto.mapper.ReservationDtoMapper;
import com.workshop.pizza.entity.Customer;
import com.workshop.pizza.entity.Reservation;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.ICustomerRepository;
import com.workshop.pizza.repository.IReservationRepository;

@Service
public class ReservationService implements ICommonService<Reservation> {

	@Autowired
	private IReservationRepository reservationRepository;

	@Autowired
	private ICustomerRepository customerRepository;

	@Autowired
	private ReservationDtoMapper reservationDtoMapper;

	@Override
	public List<Reservation> getAll() {
		return reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));
	}

	@Override
	public Reservation save(Reservation t) {
		Customer customer = customerRepository.findById(t.getCustomer().getId())
				.orElseThrow(() -> new NotFoundException("Customer doesn't found"));
		t.setReservationCode(UUIDGenerator.generate());
		t.setCustomer(customer);
		return reservationRepository.save(t);
	}

	@Override
	public Reservation update(Reservation t, int id) {
		Reservation existingReservation = reservationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Reservation doesn't exist with id: " + id));
		return reservationRepository.save(existingReservation);
	}

	@Override
	public Optional<Reservation> getById(int id) {
		return reservationRepository.findById(id);
	}

	@Override
	public void delete(int id) {
		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Reservation doesn't exists"));
		reservation.setReservationStatus("Canceled");
		reservationRepository.save(reservation);
	}

	public void changePaidStatus(int id) {
		Reservation reservation = reservationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Reservation doesn't exists"));
		reservation.setReservationStatus("Paid");
		reservationRepository.save(reservation);
	}

	public boolean checkReservationDateTime(ReservationDto reservationDto) {
		LocalDate reservationDate = reservationDto.getReservationDate();
		LocalTime reservationTime = reservationDto.getReservationTime();

		if (reservationDate.isBefore(LocalDate.now()))
			throw new BadRequestException("You can't reserve for a past date.");

		if (reservationDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
			if (reservationTime.isBefore(LocalTime.parse("11:00")) || reservationTime.isAfter(LocalTime.parse("21:00")))
				throw new BadRequestException("Reservations are only available from 11am to 21pm on Saturdays.");
		} else if (reservationDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
			if (reservationTime.isBefore(LocalTime.parse("11:00")) || reservationTime.isAfter(LocalTime.parse("22:00")))
				throw new BadRequestException("You are only allowed to book between 11am and 22pm on Sundays.");
		} else {
			if (reservationTime.isBefore(LocalTime.parse("12:00")) || reservationTime.isAfter(LocalTime.parse("21:00")))
				throw new BadRequestException("Reservations are only available from 12pm to 21pm");
		}

		Integer totalPartySize = reservationRepository.totalPartySize(reservationDate, LocalTime.parse("12:00"),
				reservationTime);
		Long partySize = (totalPartySize != null) ? totalPartySize.longValue() : 0;

		if (partySize >= 50) {
			Integer sum = reservationRepository.totalPartySize(reservationDate, reservationTime,
					reservationTime.plusHours(1));
			Long sumParty = (sum != null) ? sum.longValue() : 0;
			return (sumParty < 12);
		}

		return true;
	}

	public Reservation bookTable(ReservationDto reservationDto) {

		if (Boolean.FALSE.equals(checkReservationDateTime(reservationDto)))
			throw new BadRequestException(
					"table already booked in this date, time; please choose another time or date, thanks!");

		Reservation reservation = new Reservation();
		Customer customer = customerRepository.findById(reservationDto.getCustomer().getId())
				.orElseThrow(() -> new NotFoundException("Customer doesn't exists"));
		reservation.setReservationCode(UUIDGenerator.generate());
		reservation.setReservationDate(reservationDto.getReservationDate());
		reservation.setReservationTime(reservationDto.getReservationTime());
		reservation.setPartySize(reservationDto.getPartySize());
		reservation.setReservationStatus("Deposited");
		reservation.setCustomer(customer);
		return reservationRepository.save(reservation);
	}

	public ReservationOutput getReservation(int id) {
		return reservationRepository.findById(id).map(reservationDtoMapper)
				.orElseThrow(() -> new NotFoundException("Reservation doesn't exists"));
	}

	public List<ReservationOutput> getReservations() {
		List<Reservation> listReservation = reservationRepository
				.findAll(Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));
		return listReservation.stream().map(reservationDtoMapper).collect(Collectors.toList());
	}

	// Phân trang
	public PageDto<ReservationOutput> getAllReservations(int page, int size) {
		Pageable pageable = PageRequest.of(page, size,
				Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));
		Page<Reservation> reservationPage = reservationRepository.findAll(pageable);
		List<ReservationOutput> reservationDtos = reservationPage.getContent().stream().map(reservationDtoMapper)
				.collect(Collectors.toList());
		long totalElements = reservationPage.getTotalElements();
		int totalPages = reservationPage.getTotalPages();
		return new PageDto<>(reservationDtos, totalElements, totalPages);
	}

	// Tìm kiếm và phân trang
	public PageDto<ReservationOutput> getReservationBySearch(int page, int size, String query) {
		Pageable pageable = PageRequest.of(page, size,
				Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));
		Page<Reservation> reservationPage = reservationRepository.findByReservationCodeContaining(query, pageable);
		List<ReservationOutput> reservationDtos = reservationPage.getContent().stream().map(reservationDtoMapper)
				.collect(Collectors.toList());
		long totalElements = reservationPage.getTotalElements();
		int totalPages = reservationPage.getTotalPages();
		return new PageDto<>(reservationDtos, totalElements, totalPages);
	}

}
