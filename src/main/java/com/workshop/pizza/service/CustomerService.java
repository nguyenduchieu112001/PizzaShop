package com.workshop.pizza.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workshop.pizza.controller.form.ForgotPasswordForm;
import com.workshop.pizza.controller.form.CustomerRequest;
import com.workshop.pizza.dto.BillDto;
import com.workshop.pizza.dto.CustomerDto;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.dto.ReservationDto;
import com.workshop.pizza.dto.mapper.BillDtoMapper;
import com.workshop.pizza.dto.mapper.CustomerDtoMapper;
import com.workshop.pizza.dto.mapper.ReservationDtoMapper;
import com.workshop.pizza.entity.Bill;
import com.workshop.pizza.entity.Customer;
import com.workshop.pizza.entity.Reservation;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.ConflictException;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IBillRepository;
import com.workshop.pizza.repository.ICustomerRepository;
import com.workshop.pizza.repository.IReservationRepository;

@Service
public class CustomerService implements ICommonService<Customer> {


	@Autowired
	private ICustomerRepository customerRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private IReservationRepository reservationRepository;

	@Autowired
	private IBillRepository billRepository;

	@Autowired
	private CustomerDtoMapper customerDtoMapper;

	@Autowired
	private ReservationDtoMapper reservationDtoMapper;

	@Autowired
	private BillDtoMapper billDtoMapper;

	@Autowired(required = true)
	private EmailServiceImpl emailServiceImpl;

	@Override
	public List<Customer> getAll() {
		return customerRepository.findAll();
	}

	public PageDto<CustomerDto> getAllCustomers(int page, int size, String query) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "customerName"));
		Page<Customer> customerPage = customerRepository
				.findByCustomerNameContainingOrUsernameContainingOrEmailContainingOrPhoneNumberContaining(pageable,
						query, query, query, query);
		List<CustomerDto> customerDtos = customerPage.getContent().stream().map(customerDtoMapper)
				.collect(Collectors.toList());
		long totalElements = customerPage.getTotalElements();
		int totalPages = customerPage.getTotalPages();
		return new PageDto<>(customerDtos, totalElements, totalPages);
	}

	@Override
	public Customer save(Customer t) {
		t.setPassword(passwordEncoder.encode(t.getPassword()));
		if (customerRepository.existsByUsername(t.getUsername()))
			throw new ConflictException("Username already exists");
		if (customerRepository.existsByEmail(t.getEmail()))
			throw new ConflictException("Email already exists");
		if (customerRepository.existsByPhoneNumber(t.getPhoneNumber()))
			throw new ConflictException("PhoneNumber already exists");
		return customerRepository.save(t);
	}

	@Override
	public Customer update(Customer t, int id) {
		if (customerRepository.existsByUsername(t.getUsername()))
			throw new ConflictException("Username already exists");
		if (customerRepository.existsByEmail(t.getEmail()))
			throw new ConflictException("Email already exists");
		if (customerRepository.existsByPhoneNumber(t.getPhoneNumber()))
			throw new ConflictException("PhoneNumber already exists");
		Customer existingCustomer = customerRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Customer doesn't exist with id: " + id));
		existingCustomer.setCustomerName(t.getCustomerName());
		existingCustomer.setPassword(passwordEncoder.encode(t.getPassword()));
		existingCustomer.setAddress(t.getAddress());
		existingCustomer.setPhoneNumber(t.getPhoneNumber());
		return customerRepository.save(existingCustomer);
	}

	@Override
	public Optional<Customer> getById(int id) {
		if (customerRepository.findById(id).isEmpty())
			throw new NotFoundException("Customer doesn't exist");
		return customerRepository.findById(id);
	}

	@Override
	public void delete(int id) {
		// nope
	}

	public int findByEmail(String email) {
		Customer customer = customerRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException("Customer doesn't exists with email: " + email));
		return customer.getId();
	}

	public Customer findByUsername(String username) {
		return customerRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User name not found"));
	}

	public Customer setCustomer(Customer customer) {
		Customer newCustomer = new Customer();
		newCustomer.setId(customer.getId());
		newCustomer.setCustomerName(customer.getCustomerName());
		newCustomer.setEmail(customer.getEmail());
		newCustomer.setPhoneNumber(customer.getPhoneNumber());
		newCustomer.setAddress(customer.getAddress());

		return newCustomer;
	}

	public PageDto<ReservationDto> getReservations(int id, int page, int size) {
		Pageable pageable = PageRequest.of(page, size,
				Sort.by(Sort.Direction.DESC, "reservationDate", "reservationTime"));
		Page<Reservation> reservationPage = reservationRepository.findByCustomerId(id, pageable);
		List<ReservationDto> reservationDtos = reservationPage.getContent().stream().map(reservationDtoMapper)
				.collect(Collectors.toList());
		long totalElements = reservationPage.getTotalElements();
		int totalPages = reservationPage.getTotalPages();
		return new PageDto<>(reservationDtos, totalElements, totalPages);

	}

	public PageDto<BillDto> getBills(int id, int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Bill> billPage = billRepository.findByCustomerId(id, pageable);
		List<BillDto> billDtos = billPage.getContent().stream().map(billDtoMapper).collect(Collectors.toList());
		long totalElements = billPage.getTotalElements();
		int totalPages = billPage.getTotalPages();
		return new PageDto<>(billDtos, totalElements, totalPages);
	}

//	@Cacheable("customer_code")
//	private boolean isValidCode(String recipient, String code) {
//		Cache cache = cacheManager.getCache("customer_code");
//		// Retrieve cached code and expiration time
//		CodeAndExpiration cachedCodeAndExpiration = cache.get(recipient, CodeAndExpiration.class);
//		System.out.println(cache);
	// Check if cached code and expiration time are valid
//		return !(cachedCodeAndExpiration == null || !cachedCodeAndExpiration.getCode().equals(code)
//				|| LocalTime.now().isAfter(cachedCodeAndExpiration.getExpirationTime()));
//	}

//	@CacheEvict(value = "customer_code", allEntries = true)
	public void forgotPassword(ForgotPasswordForm changePasswordForm, int id) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Customer doesn't exists"));
		System.out.println(emailServiceImpl.isValidCode(changePasswordForm.getCode()));
		if (Boolean.FALSE.equals(emailServiceImpl.isValidCode(changePasswordForm.getCode()))) {
			throw new BadRequestException("Code is expired or not correct");
		} else if (changePasswordForm.getPassword() != null) {
			customer.setPassword(passwordEncoder.encode(changePasswordForm.getPassword()));
		}
		customerRepository.save(customer);
	}

	public void changePassword(Customer customer, String password) {
		customer.setPassword(passwordEncoder.encode(password));
		customerRepository.save(customer);
	}

	public CustomerDto getInformation(int id) {
		return customerRepository.findById(id).map(customerDtoMapper)
				.orElseThrow(() -> new NotFoundException("Customer doesn't exists"));
	}

	public Customer changeInformation(CustomerRequest customerDto, int id) {
		Customer customer = customerRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Customer doesn't exists"));
		if (customerDto.getCustomerName() != null)
			customer.setCustomerName(customerDto.getCustomerName());
		if (customerDto.getEmail() != null && !customerDto.getEmail().equals(customer.getEmail())) {
			if (!customerRepository.existsByEmail(customerDto.getEmail()))
				customer.setEmail(customerDto.getEmail());
			else
				throw new ConflictException("Email already exists");
		}
		if (customerDto.getAddress() != null)
			customer.setAddress(customerDto.getAddress());
		if (customerDto.getPhoneNumber() != null && !customerDto.getPhoneNumber().equals(customer.getPhoneNumber())) {
			if (!customerRepository.existsByPhoneNumber(customerDto.getPhoneNumber()))
				customer.setPhoneNumber(customerDto.getPhoneNumber());
			else
				throw new ConflictException("PhoneNumber already exists");
		}
		return customerRepository.save(customer);
	}

}
