package com.workshop.pizza.config;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class CustomerDetails implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private String username;
	private String password;
	private String customerName;
	private String email;
	private String phoneNumber;
	private String address;
	private Collection<? extends GrantedAuthority> authorities;

	public CustomerDetails(int id, String username, String password, String customerName, String email, String phoneNumber,
			String address, Collection<? extends GrantedAuthority> authorities) {
		super();
		this.id=id;
		this.username = username;
		this.password = password;
		this.customerName = customerName;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
