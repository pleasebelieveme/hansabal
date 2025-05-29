package org.example.hansabal.common.jwt;

import org.example.hansabal.domain.users.entity.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	private static final long expiration = 1000L * 60 * 30;

	public String createToken(Long id, UserRole userRole){
		return Jwts.builder()
			.setSubject(String.valueOf(id))
			.claim("userRole", userRole.name())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis()+expiration))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
			.compact();
	}

	public UserAuth extractUserAuth(String token){
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes())
			.build()
			.parseClaimsJws(token)
			.getBody();

		return new UserAuth(Long.parseLong(claims.getSubject()), UserRole.valueOf(claims.get("userRole",String.class)));
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
}