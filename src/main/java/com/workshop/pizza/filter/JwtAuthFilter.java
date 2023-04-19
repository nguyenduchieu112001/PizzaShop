package com.workshop.pizza.filter;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workshop.pizza.config.CustomUserDetailService;
import com.workshop.pizza.exception.BadRequestException;
import com.workshop.pizza.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private CustomUserDetailService userDetailService;
	
	private static ObjectMapper mapper = new ObjectMapper();
	
	private static void checkExpiredJwtException(HttpServletResponse response) throws IOException {
		HttpStatus httpStatus = HttpStatus.UNAUTHORIZED; // 401
		Map<String, Object> data = new HashMap<>();
		data.put("message", "JWT expired");
		data.put("status", HttpStatus.UNAUTHORIZED);
		data.put("timestamp", ZonedDateTime.now(ZoneId.of("Z")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME));

		// setting the response HTTP status code
		response.setStatus(httpStatus.value());
		response.setContentType("application/json");

		String json = mapper.writeValueAsString(data);
		response.getWriter().write(json);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		try {
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
				username = jwtService.extractUsername(token);

			}
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetail = userDetailService.loadUserByUsername(username);
				if (Boolean.TRUE.equals(jwtService.validateToken(token, userDetail))) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetail,
							null, userDetail.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
				else
					throw new BadRequestException("Invalid username or password.");
			}
		} catch (ExpiredJwtException | SignatureException e) {
			checkExpiredJwtException(response);
            return;
		}
		filterChain.doFilter(request, response);

	}

}
