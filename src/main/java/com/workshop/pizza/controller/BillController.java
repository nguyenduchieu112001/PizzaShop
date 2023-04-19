package com.workshop.pizza.controller;

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
import com.workshop.pizza.controller.form.BillRequest;
import com.workshop.pizza.dto.BillDto;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.service.BillService;
import com.workshop.pizza.service.PayPalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/bill")
public class BillController {

	@Autowired
	private BillService billService;

	@Autowired
	private PayPalService payPalService;
	
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageDto<BillDto>> getBillsBySearch(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "25") int size, @RequestParam(defaultValue = "") String query) {
		if("".equals(query))
			return ResponseEntity.ok(billService.getAllBills(page-1, size));
		else return ResponseEntity.ok(billService.getBillsBySearch(page-1, size, query));
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<BillDto> getBill(@PathVariable int id) {
		return ResponseEntity.ok(billService.getBill(id));
	}

	@PostMapping("/order/processing")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> orderBillProcessing(@RequestBody BillRequest billDto) {
		try {
			billService.orderBillProcessing(billDto);
			throw new OkException("Order successfully");
		} catch (NotFoundException e) { 
			throw e; 
		} catch (BadRequestException e) {
			throw new BadRequestException("Error creating order");
		}

	}
	
	@PostMapping("/order/deposited")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> orderBillDeposited(@RequestBody BillRequest billDto) {
		try {
			billService.orderBillDeposited(billDto);
			throw new OkException("Order successfully");
		} catch (NotFoundException e) { 
			throw e; 
		} catch (BadRequestException e) {
			throw new BadRequestException("Error creating order");
		}

	}

	@PostMapping("/pay-order")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> createOrderOnline(@Valid @RequestBody BillRequest bill) {
		try {
			HttpResponse<Order> response = payPalService.createOrderOnline(bill);
			String orderId = response.result().id();
			return ResponseEntity.ok(orderId);
		} catch (Exception e) {
			throw new BadRequestException("Error creating order");
		}
	}

	@PutMapping("/change-status-shipping/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<BillDto> changeShippingStatus(@PathVariable int id) {
		billService.changeShippingStatus(id);
		return ResponseEntity.ok(billService.getBill(id));
	}

	@PutMapping("/change-status-delivered/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<BillDto> changeDeliveredStatus(@PathVariable int id) {
		billService.changeDeliveredStatus(id);
		return ResponseEntity.ok(billService.getBill(id));
	}

	@PutMapping("/change-status-paid/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<BillDto> changePaidStatus(@PathVariable int id) {
		billService.changePaidStatus(id);
		return ResponseEntity.ok(billService.getBill(id));
	}

	@PutMapping("/change-status-canceled/{id}")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<BillDto> changeCanceledStatus(@PathVariable int id) {
		billService.delete(id);
		return ResponseEntity.ok(billService.getBill(id));
	}
}
