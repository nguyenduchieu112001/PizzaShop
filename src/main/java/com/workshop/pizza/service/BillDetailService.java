package com.workshop.pizza.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.workshop.pizza.entity.BillDetail;
import com.workshop.pizza.repository.IBillDetailRepository;

@Service
public class BillDetailService implements ICommonService<BillDetail> {

	@Autowired
	private IBillDetailRepository billDetailRepository;

	@Override
	public List<BillDetail> getAll() {
		return billDetailRepository.findAll();
	}

	@Override
	public BillDetail save(BillDetail t) {
		return billDetailRepository.save(t);
	}

	@Override
	public BillDetail update(BillDetail t, int id) {
		return null;
	}

	@Override
	public Optional<BillDetail> getById(int id) {
		return billDetailRepository.findById(id);
	}

	@Override
	public void delete(int id) {
		// this method is empty
	}
}
