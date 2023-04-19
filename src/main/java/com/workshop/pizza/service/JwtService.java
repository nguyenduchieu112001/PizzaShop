package com.workshop.pizza.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.workshop.pizza.entity.Customer;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	public static final String SECRECT = "33743677397A244326452948404D635166546A576E5A7234753778214125442A";

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public int extractId(String token) {
		return extractClaim(token, claims -> (int) claims.get("id"));
	}

	public String extractCustomerName(String token) {
		return extractClaim(token, claims -> (String) claims.get("customerName"));
	}

	public Date extractExpriration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
	}

	public String generateToken(String userName, String customerName) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userName, customerName);
	}

	private String createToken(Map<String, Object> claims, String userName, String customerName) {
		return Jwts.builder().setClaims(claims).setSubject(userName).claim("customerName", customerName)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	public String generateTokenCustomer(Customer customer) {
		Map<String, Object> claims = new HashMap<>();
		return createTokenCustomer(claims, customer);
	}

	private String createTokenCustomer(Map<String, Object> claims, Customer customer) {

		return Jwts.builder().setClaims(claims).setSubject(customer.getUsername()).claim("id", customer.getId())
//				.claim("customerName", customer.getCustomerName()).claim("email", customer.getEmail())
//				.claim("phoneNumber", customer.getPhoneNumber()).claim("address", customer.getAddress())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRECT);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractExpriration(token).before(new Date());
	}
}
