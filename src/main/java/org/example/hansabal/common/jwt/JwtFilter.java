package org.example.hansabal.common.jwt;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.Cookie;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final RedisRepository redisRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();
		log.info(":흰색_확인_표시: 요청 URI: {}", uri);
		String token = extractTokenFromCookies(request); // :흰색_확인_표시: 쿠키에서 추출
		log.info(":열쇠와_잠긴_자물쇠: 추출된 accessToken: {}", token);
		if (token == null || token.isBlank()) {
			filterChain.doFilter(request, response);
			return;
		}
		if (redisRepository.validateKey(token)) {
			log.warn(":x: 블랙리스트 토큰 접근 시도");
			handleAuthFailure(request, response, "이미 로그아웃된 토큰입니다.");
			return;
		}
		try {
			if (jwtUtil.validateToken(token)) {
				UserAuth userAuth = jwtUtil.extractUserAuth(token);
				List<SimpleGrantedAuthority> authorities = List.of(
						new SimpleGrantedAuthority("ROLE_" + userAuth.getUserRole().name())
				);
				UsernamePasswordAuthenticationToken authToken =
						new UsernamePasswordAuthenticationToken(userAuth, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authToken);
				log.info(":흰색_확인_표시: 인증 성공: userId={}, role={}", userAuth.getId(), userAuth.getUserRole());
				// 로그인 상태에서 /login 접근 시 홈으로 리다이렉트
				if (uri.equals("/login")) {
					response.sendRedirect("/home");
					return;
				}
			}
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 예외 발생", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 접근입니다.");
			return;
		}
		filterChain.doFilter(request, response);
	}
	// 🔒 인증 실패 처리: 페이지 vs API 구분
	private void handleAuthFailure(
			HttpServletRequest request,
			HttpServletResponse response,
			String message
	) throws IOException {

		String accept = request.getHeader("Accept");
		boolean isApiRequest = accept != null && accept.contains("application/json");

		if (isApiRequest) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("{\"error\": \"" + message + "\"}");
		} else {
			// 웹 요청일 경우 로그인 페이지로 리디렉션
			response.sendRedirect("/login?error");
		}
	}
	private String extractTokenFromCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("accessToken".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
