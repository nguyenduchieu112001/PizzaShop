package com.workshop.pizza.repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.workshop.pizza.entity.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Integer> {

	@Query(value = "Select MAX(id) from reservation", nativeQuery = true)
	int findMaxId();

	Page<Reservation> findByCustomerId(int customerId, Pageable pageable);

	@Query(value = "Select SUM(party_size) as total_party_size"
			+ " from reservation where reservation_date = :date "
			+ "and reservation_time between :timeStart and :timeEnd "
			+ "and reservation_status = 'Deposited' ", nativeQuery = true)
	Integer totalPartySize(LocalDate date, LocalTime timeStart, LocalTime timeEnd);

	Page<Reservation> findByReservationCodeContaining(String query, Pageable pageable);
	
	

}
