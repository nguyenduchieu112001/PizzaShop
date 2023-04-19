package com.workshop.pizza.exception;

public class MethodNotAllowedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MethodNotAllowedException(String name) {
		super(name);
	}

}
