package org.example.hansabal.common.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.example.hansabal.domain.auth.repository.TokenRepository;
import org.example.hansabal.domain.auth.util.JwtUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final TokenRepository tokenRepository;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !SecurityUrlMatcher.isRefreshUrl(request.getRequestURI());
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException,
		ServletException {

		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			setErrorResponse(response, "RefreshToken 쿠키가 없습니다.");
			return;
		}

		String refreshToken = null;
		for (Cookie cookie : cookies) {
			if ("refreshToken".equals(cookie.getName())) {
				refreshToken = cookie.getValue();
				break;
			}
		}

		try {
			Claims claims = jwtUtil.extractClaims(refreshToken);

			if (claims == null) {
				setErrorResponse(response, "유효하지 않은 RefreshToken입니다.");
				return;
			}

			String userId = claims.getSubject();
			if (!tokenRepository.isRefreshTokenValid(userId, refreshToken)) {
				setErrorResponse(response, "유효하지 않은 RefreshToken입니다.");
				return;
			}

			List<SimpleGrantedAuthority> authorities =
				List.of(new SimpleGrantedAuthority("ROLE_GUEST"));

			CustomAuthenticationToken authentication =
				new CustomAuthenticationToken(userId, null, authorities);

			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			setErrorResponse(response, "토큰이 만료되었습니다.");
		} catch (Exception e) {
			setErrorResponse(response, "인증 과정에서 오류가 발생했습니다.");
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
