package com.workshop.pizza.dto.mapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.workshop.pizza.dto.BillDetailDto;
import com.workshop.pizza.dto.BillDto;
import com.workshop.pizza.dto.CustomerDto;
import com.workshop.pizza.entity.Bill;
import com.workshop.pizza.entity.BillDetail;
import com.workshop.pizza.entity.Customer;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IBillDetailRepository;
import com.workshop.pizza.repository.ICustomerRepository;

@Service
public class BillDtoMapper implements Function<Bill, BillDto> {

	@Autowired
	private IBillDetailRepository billDetailRepository;

	@Autowired
	private ICustomerRepository customerRepository;

	@Autowired(required = true)
	private BillDetailDtoMapper billDetailDtoMapper;

	@Override
	public BillDto apply(Bill t) {
		Customer customer = customerRepository.findById(t.getCustomer().getId())
				.orElseThrow(() -> new NotFoundException("Customer doesn't exists"));
		CustomerDto newCustomer = new CustomerDto(customer.getId(), customer.getUsername(), customer.getEmail(),
				customer.getPhoneNumber(), customer.getAddress(), customer.getUsername());
		List<BillDetail> listBillDetail = billDetailRepository.findByBillId(t.getId());
		Set<BillDetailDto> billDetails = new HashSet<>();
		billDetails.addAll(listBillDetail.stream().map(billDetailDtoMapper).collect(Collectors.toSet()));
		return new BillDto(t.getId(), billDetailRepository.subtotalBill(t.getId()), t.getBillStatus(),
				t.getCreatedAt(), newCustomer, billDetails);
	}

}
