package com.workshop.pizza.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workshop.pizza.controller.form.AuthRequest;
import com.workshop.pizza.entity.Admin;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.exception.OkException;
import com.workshop.pizza.service.AdminService;
import com.workshop.pizza.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

	@Autowired
	private AdminService adminService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtService jwt;

	@PostMapping("/sign-up")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<Admin> signUp(@RequestBody Admin admin) {
		return ResponseEntity.ok(adminService.save(admin));
	}

	@PostMapping("/login")
	@PreAuthorize("permitAll()")
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		String name = adminService.findByUsername(authRequest.getUsername()).getAdminName();
		if (authentication.isAuthenticated())
			throw new OkException(jwt.generateToken(authRequest.getUsername(), name));

		throw new BadRequestException("Invalid username or password.");
	}
	
	@GetMapping("/name")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String getName(HttpServletRequest request){
		String authorizationHeader = request.getHeader("Authorization");
		String token = authorizationHeader.substring(7); // remove "Bearer " prefix
	    throw new OkException(jwt.extractCustomerName(token)) ;
	}
}
