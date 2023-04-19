package com.workshop.pizza.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.workshop.pizza.controller.form.BillDetailRequest;
import com.workshop.pizza.controller.form.BillRequest;
import com.workshop.pizza.controller.form.UUIDGenerator;
import com.workshop.pizza.dto.BillDto;
import com.workshop.pizza.dto.PageDto;
import com.workshop.pizza.dto.mapper.BillDtoMapper;
import com.workshop.pizza.entity.Bill;
import com.workshop.pizza.entity.BillDetail;
import com.workshop.pizza.entity.Customer;
import com.workshop.pizza.entity.Product;
import com.workshop.pizza.entity.ProductSize;
import com.workshop.pizza.entity.Size;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IBillDetailRepository;
import com.workshop.pizza.repository.IBillRepository;
import com.workshop.pizza.repository.ICustomerRepository;
import com.workshop.pizza.repository.IProductRepository;
import com.workshop.pizza.repository.IProductSizeRepository;
import com.workshop.pizza.repository.ISizeRepository;

@Service
public class BillService implements ICommonService<Bill> {

	@Autowired
	private IBillRepository billRepository;

	@Autowired
	private IProductRepository productRepository;
	
	@Autowired
	private ISizeRepository sizeRepository;
	
	@Autowired
	private IProductSizeRepository productSizeRepository;

	@Autowired
	private ICustomerRepository customerRepository;

	@Autowired
	private IBillDetailRepository billDetailRepository;

	@Autowired
	private BillDtoMapper billDtoMapper;

	@Override
	public List<Bill> getAll() {
		return billRepository.findAll();
	}

	@Override
	public Bill save(Bill t) {
		t.setBillCode(UUIDGenerator.generate());
		return billRepository.save(t);
	}

	@Override
	public Bill update(Bill t, int id) {
		Bill existingBill = billRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Bill doesn't exist with id: " + id));
		existingBill.setBillStatus(t.getBillStatus());
		return billRepository.save(existingBill);
	}

	@Override
	public Optional<Bill> getById(int id) {
		return billRepository.findById(id);
	}

	@Override
	public void delete(int id) {
		Bill existingBill = billRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Bill doesn't exist with id: " + id));
		existingBill.setBillStatus("Canceled");
		billRepository.save(existingBill);
	}

	public void changeShippingStatus(int id) {
		Bill existingBill = billRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Bill doesn't exist with id: " + id));
		existingBill.setBillStatus("Shipping");
		billRepository.save(existingBill);
	}

	public void changeDeliveredStatus(int id) {
		Bill existingBill = billRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Bill doesn't exist with id: " + id));
		existingBill.setBillStatus("Delivered");
		billRepository.save(existingBill);
	}

	public void changePaidStatus(int id) {
		Bill existingBill = billRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Bill doesn't exist with id: " + id));
		existingBill.setBillStatus("Paid");
		billRepository.save(existingBill);
	}

	//đặt hàng không trả trước
	public void orderBillProcessing(BillRequest billDto) {
		Bill bill = new Bill();
		bill.setBillStatus("Processing");
		Customer customer = customerRepository.findById(billDto.getCustomer().getId())
				.orElseThrow(() -> new NotFoundException("Customer doesn't exist"));
		bill.setCustomer(customer);
		bill.setBillCode(UUIDGenerator.generate());
		
		for (BillDetailRequest billDetailDto : billDto.getBillDetails()) {
			BillDetail billDetail = new BillDetail();
			billDetail.setQuantity(billDetailDto.getQuantity());
			Product product = productRepository.findById(billDetailDto.getProduct().getId())
					.orElseThrow(() -> new NotFoundException("Product doesn't exist"));
			Size size = sizeRepository.findById(billDetailDto.getSize().getId())
					.orElseThrow(() -> new NotFoundException("Size doesn't exist"));
			ProductSize productSize = productSizeRepository.findByProductAndSize(product, size);
			billDetail.setProductSize(productSize);
			billDetail.setBill(bill);
			billRepository.save(bill);
			billDetailRepository.save(billDetail);
		}
	}
	
	//Đặt hàng đã trả trước
	public void orderBillDeposited(BillRequest billDto) {
		Bill bill = new Bill();
		bill.setBillStatus("Deposited");
		Customer customer = customerRepository.findById(billDto.getCustomer().getId())
				.orElseThrow(() -> new NotFoundException("Customer doesn't exist"));
		bill.setCustomer(customer);
		bill.setBillCode(UUIDGenerator.generate());
		
		for (BillDetailRequest billDetailDto : billDto.getBillDetails()) {
			BillDetail billDetail = new BillDetail();
			billDetail.setQuantity(billDetailDto.getQuantity());
			Product product = productRepository.findById(billDetailDto.getProduct().getId())
					.orElseThrow(() -> new NotFoundException("Product doesn't exist"));
			Size size = sizeRepository.findById(billDetailDto.getSize().getId())
					.orElseThrow(() -> new NotFoundException("Size doesn't exist"));
			ProductSize productSize = productSizeRepository.findByProductAndSize(product, size);
			billDetail.setProductSize(productSize);
			billDetail.setBill(bill);
			billRepository.save(bill);
			billDetailRepository.save(billDetail);
		}
	}

	public BillDto getBill(int id) {
		return billRepository.findById(id).map(billDtoMapper)
				.orElseThrow(() -> new NotFoundException("Bill doesn't exist"));
	}

	public List<BillDto> getBills() {
		List<Bill> listBill = billRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		return listBill.stream().map(billDtoMapper).collect(Collectors.toList());
	}
	
	//Phân trang
	public PageDto<BillDto> getAllBills(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Bill> billPage = billRepository.findAll(pageable);
		List<BillDto> billDtos = billPage.getContent().stream().map(billDtoMapper).collect(Collectors.toList());
		long totalElements = billPage.getTotalElements();
		int totalPages = billPage.getTotalPages();
		return new PageDto<>(billDtos, totalElements, totalPages);
	}
	
	//Phân trang và tìm kiếm
	public PageDto<BillDto> getBillsBySearch(int page, int size, String query) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Bill> billPage = billRepository.findByBillCodeContaining(query, pageable);
		List<BillDto> billDtos = billPage.getContent().stream().map(billDtoMapper).collect(Collectors.toList());
		long totalElements = billPage.getTotalElements();
		int totalPages = billPage.getTotalPages();
		return new PageDto<>(billDtos, totalElements, totalPages);
	}
}
