package com.workshop.pizza.config;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		HttpStatus httpStatus = HttpStatus.FORBIDDEN; // 403
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Access denied");
        data.put("status", HttpStatus.FORBIDDEN);
        data.put("timestamp", ZonedDateTime.now(ZoneId.of("Z")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

        // setting the response HTTP status code
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");

        // serializing the response body in JSON
        response.getOutputStream().println(objectMapper.writeValueAsString(data));

	}

}
