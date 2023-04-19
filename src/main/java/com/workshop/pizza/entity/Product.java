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
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@NotNull(message = "Product name shouldn't be null")
	@Length(min = 5, max = 250, message = "Product name must have 5-250 characters")
	private String productName;

	@NotNull(message = "Price must not be null")
	@DecimalMin(value = "0", message = "Price must be atleast 0.00")
	@DecimalMax(value = "1000000", message = "Price should not be greater than 1000000")
	private double price;

	@NotNull(message = "Description must not be null")
	@Length(min = 5, message = "Description has at least 5 characters")
	private String description;
	
	private String image;

	@CreatedDate
	private LocalDate createdAt;

	@LastModifiedDate
	private LocalDate updatedAt;
	
	@JsonIgnore
	private LocalDate deletedAt;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_type_id", referencedColumnName = "id")
	@NotNull(message = "Product must have a product type")
	private ProductType productType;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JsonIgnore
	private Set<ProductSize> productSizes;
}
