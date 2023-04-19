package com.workshop.pizza.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {

	private List<T> content;
	private long totalElement;
	private int totalPages;
}
