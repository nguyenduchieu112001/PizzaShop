package com.workshop.pizza.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.workshop.pizza.entity.Admin;
import com.workshop.pizza.entity.Customer;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.repository.IAdminRepository;
import com.workshop.pizza.repository.ICustomerRepository;

@Component
public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private ICustomerRepository customerRepository;

	@Autowired
	private IAdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Collection<GrantedAuthority> authorities = null;

		Optional<Admin> admin = adminRepository.findByUsername(username);
		if (!admin.isEmpty()) {
			authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"));
			return new CustomUserDetails(admin.get().getUsername(), admin.get().getPassword(),
					admin.get().getAdminName(), authorities);
		} else {
			Optional<Customer> customer = customerRepository.findByUsername(username);
			if (!customer.isEmpty()) {
				authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
				return new CustomerDetails(customer.get().getId(), customer.get().getUsername(), customer.get().getPassword(),
						customer.get().getCustomerName(), customer.get().getEmail(), customer.get().getPhoneNumber(),
						customer.get().getAddress(), authorities);

			}
		}

		throw new BadRequestException("Invalid username or password.");
	}

}
