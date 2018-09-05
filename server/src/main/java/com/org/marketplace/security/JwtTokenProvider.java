package com.org.marketplace.security;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Token provider for JWT security
 * 
 * @author gauravkahadane
 *
 */
@Component
public class JwtTokenProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationInMs}")
	private int jwtExpirationInMs;

	/**
	 * Generates a JWT token
	 * 
	 * @param authentication contains information of the core user of application
	 * @return JWT token
	 */
	public String generateToken(Authentication authentication) {

		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		return Jwts.builder().setSubject(Long.toString(userPrincipal.getId())).setIssuedAt(new Date())
				.setExpiration(expiryDate).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	/**
	 * Retrieves user id from JWT claim
	 * 
	 * @param token
	 * @return user id
	 */
	public Long getUserIdFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

		return Long.parseLong(claims.getSubject());
	}

	/**
	 * Validates JWT token
	 * 
	 * @param authToken represents a JWT token
	 * @return true if the token is valid otherwise false
	 */
	public boolean validateToken(String authToken) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException e) {
			LOGGER.error("Invalid JWT signature: " + e);
			throw e;
		} catch (MalformedJwtException e) {
			LOGGER.error("Invalid JWT token: " + e);
			throw e;
		} catch (ExpiredJwtException e) {
			LOGGER.error("Expired JWT token: " + e);
			throw e;
		} catch (UnsupportedJwtException e) {
			LOGGER.error("Unsupported JWT token: " + e);
			throw e;
		} catch (IllegalArgumentException e) {
			LOGGER.error("JWT claims string is empty: " + e);
			throw e;
		}
	}
}
