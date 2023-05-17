package com.workshop.pizza.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.pizza.controller.form.AuthRequest;
import com.workshop.pizza.controller.form.ChangePasswordForm;
import com.workshop.pizza.controller.form.CustomerRequest;
import com.workshop.pizza.controller.form.EmailDetails;
import com.workshop.pizza.controller.form.ForgotPasswordForm;
import com.workshop.pizza.dto.BillDto;
import com.workshop.pizza.dto.CustomerDto;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.dto.ReservationDto;
import com.workshop.pizza.entity.Customer;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.service.CustomerService;
import com.workshop.pizza.service.EmailServiceImpl;
import com.workshop.pizza.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmailServiceImpl emailServiceImpl;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwt;

	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<PageDto<CustomerDto>> getCustomers(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "") String query) {
		return ResponseEntity.ok(customerService.getAllCustomers(page - 1, size, query));
	}

	@PostMapping("/register")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Customer> register(@Valid @RequestBody Customer customer) {
		customerService.save(customer);
		throw new OkException("Register successful!");
	}

	@GetMapping("/reservation/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<PageDto<ReservationDto>> getReservations(@PathVariable int id,
			@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(customerService.getReservations(id, page - 1, size));
	}

	@GetMapping("/bill/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<PageDto<BillDto>> getBills(@PathVariable int id, @RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {
		return ResponseEntity.ok(customerService.getBills(id, page - 1, size));
	}

	@PutMapping("/change-information/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<CustomerDto> changeInformation(@Valid @RequestBody CustomerRequest customerRequest,
			@PathVariable int id) {
		customerService.changeInformation(customerRequest, id);
		return ResponseEntity.ok(customerService.getInformation(id));
	}

	@PutMapping("/forgot-password/{id}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<String> changePassword(@Valid @RequestBody ForgotPasswordForm forgot, @PathVariable int id) {
		customerService.forgotPassword(forgot, id);
		throw new OkException("Change Password successful!");
	}

	@PutMapping("/change-password")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<String> changePassword(@RequestBody ChangePasswordForm form) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(form.getUsername(), form.getPassword()));
		Customer customer = customerService.findByUsername(form.getUsername());
		if (authentication.isAuthenticated()) {
			customerService.changePassword(customer, form.getNewPassword());
			throw new OkException("Change password successfully");
		} else
			throw new BadRequestException("Invalid password.");
	}

	@PostMapping("/send-mail")
	@PreAuthorize("permitAll()")
	public ResponseEntity<String> sendMail(@RequestParam String recipient) {
		emailServiceImpl.sendMail(recipient);
		throw new OkException("Mail Sent Successfully");
	}

	// Sending email with attachment
	@PostMapping("/sendMailWithAttachment")
	public ResponseEntity<String> sendMailWithAttachment(@RequestBody EmailDetails details) {
		String status = emailServiceImpl.sendMailWithAttachment(details);

		throw new OkException(status);
	}

	@PreAuthorize("permitAll()")
	@PostMapping("/authenticate")
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		Customer customer = customerService.findByUsername(authRequest.getUsername());
		if (authentication.isAuthenticated()) {
			throw new OkException(jwt.generateTokenCustomer(customer));
		}

		throw new BadRequestException("Invalid username or password.");
	}

	@GetMapping("/information")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public ResponseEntity<CustomerDto> getName(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7); // remove "Bearer " prefix
		int id = jwt.extractId(token);
		CustomerDto customer = customerService.getInformation(id);

		return ResponseEntity.ok(customer);
	}

	@GetMapping("/find-by-email")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Integer> findByEmail(@RequestParam String email) {
		int id = customerService.findByEmail(email);
		throw new OkException(Integer.toString(id));
	}
}
