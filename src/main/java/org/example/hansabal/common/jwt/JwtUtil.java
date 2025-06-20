package org.example.hansabal.common.jwt;

import jakarta.servlet.http.Cookie;
import org.example.hansabal.domain.users.entity.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	private static final long EXPIRATION = 1000L * 60 * 30; // 30분
	private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 14; // 14일

	public String createToken(Long id, UserRole userRole,String nickname){
		return Jwts.builder()
			.setSubject(String.valueOf(id))
			.claim("userRole", userRole.name())
			.claim("nickname",nickname)
			.setIssuedAt(new Date())

			.setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))

			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
			.compact();
	}

	public UserAuth extractUserAuth(String token){
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes())
			.build()
			.parseClaimsJws(token)
			.getBody();

		return new UserAuth(Long.parseLong(claims.getSubject()), UserRole.valueOf(claims.get("userRole",String.class)),claims.get("nickname",
			String.class));
	}

	public boolean validateToken(String token){
		try {
			extractUserAuth(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String extractToken(HttpServletRequest request){
		String bearer = request.getHeader("Authorization");
		if(bearer != null && bearer.startsWith("Bearer ")){
			return bearer.substring(7);
		}
		// 2. accessToken 쿠키 확인
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("accessToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	public long getExpiration(String token){
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes())
			.build()
			.parseClaimsJws(token)
			.getBody();

		return claims.getExpiration().getTime() - System.currentTimeMillis();
	}

	public String createRefreshToken(Long id, UserRole role,String nickname) {
		return Jwts.builder()
			.setSubject(String.valueOf(id))
			.claim("userRole", role.name())
			.claim("nickname",nickname)
			.claim("jti", UUID.randomUUID().toString())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
			.compact();
	}

	public long getRefreshExpiration(String refreshToken) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
			.build()
			.parseClaimsJws(refreshToken)
			.getBody();

		return claims.getExpiration().getTime() - System.currentTimeMillis();
	}
}