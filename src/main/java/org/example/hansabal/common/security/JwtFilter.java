package org.example.hansabal.common.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.example.hansabal.domain.auth.util.JwtUtil;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserRepository userRepository;



	@Override
	protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws IOException, ServletException {

		String uri = httpRequest.getRequestURI();

		// ✅ "/crawl"은 JWT 없이도 접근 가능하게 설정
		if (uri.equals("/crawl") ||
				SecurityUrlMatcher.isPublicUrl(uri) ||
				SecurityUrlMatcher.isRefreshUrl(uri)) {

			filterChain.doFilter(httpRequest, httpResponse);
			return;
		}

		if (SecurityUrlMatcher.isPublicUrl(httpRequest.getRequestURI()) ||
			SecurityUrlMatcher.isRefreshUrl(httpRequest.getRequestURI())) {
			filterChain.doFilter(httpRequest, httpResponse);
			return;
		}

		String bearerJwt = httpRequest.getHeader("Authorization");

		if (bearerJwt == null) {
			setErrorResponse(httpResponse, "JWT 토큰이 필요합니다.");
			return;
		}

		String jwt = jwtUtil.substringToken(bearerJwt);

		try {
			Claims claims = jwtUtil.extractClaims(jwt);
			if (claims == null) {
				setErrorResponse(httpResponse, "유효하지 않은 토큰입니다.");
				return;
			}

			String userId = claims.getSubject();
			if(userRepository.findById(Long.parseLong(userId)).isEmpty()){
				setErrorResponse(httpResponse, "존재하지 않는 사용자입니다.");
				return;
			}

			String email = claims.get("email", String.class);
			String nickname = claims.get("nickname", String.class);
			UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

			List<SimpleGrantedAuthority> authorities =
				List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));

			CustomAuthenticationToken authentication =
				new CustomAuthenticationToken(userId, null, authorities, email, nickname);

			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(httpRequest, httpResponse);
		} catch (ExpiredJwtException e) {
			setErrorResponse(httpResponse, "토큰이 만료되었습니다.");
		} catch (Exception e) {
			setErrorResponse(httpResponse, "인증 과정에서 오류가 발생했습니다.");
		}
	}

	private void setErrorResponse(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");

		String body = new ObjectMapper().writeValueAsString(
			Map.of(
				"status", 401,
				"error", "Unauthorized",
				"message", message
			)
		);
		response.getWriter().write(body);
	}
}
