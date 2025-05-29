package org.example.hansabal.domain.auth.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.auth.exception.AuthErrorCode;
import org.example.hansabal.domain.users.entity.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;

@Component
public class JwtUtil {

	private static final String BEARER_PREFIX = "Bearer ";

	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration;
	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(Long userId, String email, String nickname, UserRole userRole) {
		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(String.valueOf(userId))
				.claim("email", email)
				.claim("userRole", userRole)
				.claim("nickname", nickname)
				.setExpiration(new Date(date.getTime() + accessTokenExpiration))
				.setIssuedAt(date) // 발급일
				.signWith(key, signatureAlgorithm) // 암호화 알고리즘
				.compact();
	}

	public String createRefreshToken(Long userId) {
		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(String.valueOf(userId))
				.setExpiration(new Date(date.getTime() + refreshTokenExpiration))
				.signWith(key, signatureAlgorithm)
				.compact();
	}


	public String substringToken(String tokenValue) {
		if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
			return tokenValue.substring(7);
		}
		throw new BizException(AuthErrorCode.NOT_FOUND_ACCESS_TOKEN);
	}

	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}