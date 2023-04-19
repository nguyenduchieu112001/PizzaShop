package com.workshop.pizza.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.workshop.pizza.entity.Admin;
import com.workshop.pizza.exception.NotFoundException;
import com.workshop.pizza.repository.IAdminRepository;

@Service
public class AdminService {

	@Autowired
	private IAdminRepository adminRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public Admin save(Admin admin) {
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		return adminRepository.save(admin);
	}

	public Admin findByUsername(String username) {
		return adminRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("Admin doesn't exists"));
	}
}
