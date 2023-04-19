package com.workshop.pizza.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Autowired
	private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		customAuthenticationFailureHandler.onAuthenticationFailure(request, response, authException);

	}

}
