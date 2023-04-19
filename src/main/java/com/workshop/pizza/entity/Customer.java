package com.workshop.pizza.entity;

import java.time.LocalDate;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(indexes = { @Index(name = "uniqueMultiIndex", columnList = "username, email, phoneNumber", unique = true) })
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull
	@Column(unique = true)
	@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_-]{4,23}$", message = "Username must begin with a letter and Letters, numbers, underscores, hyphens allowed")
	private String username;

	@Length(min = 10, message = "Customer name must at least 10 characters")
	@NotNull(message = "Customer name shouldn't be null")
	private String customerName;

	@NotBlank(message = "Email shouldn't be blank")
	@Email(message = "Invalid Email")
	@NotNull
	@Column(unique = true)
	private String email;

	@NotNull
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character (!@#$%), no spaces and the minimum length is 8")
	private String password;

	@NotNull(message = "Customer address should not be null")
	@Length(min = 10)
	private String address;

	@Pattern(regexp = "^(\\+84|0)\\d{9,10}$", message = "Invalid phone number. Phone number must start with '+84' or '0' and must be 9 or 10 digits long.")
	@NotBlank(message = "Phone Number must not be blank")
	@Column(unique = true)
	private String phoneNumber;

	@CreatedDate
	private LocalDate createdAt;

	@LastModifiedDate
	private LocalDate updatedAt;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Bill> bills;

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<Reservation> reservations;
}
