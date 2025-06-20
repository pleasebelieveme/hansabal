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
		log.info("✅ 요청 URI: {}", uri);
		String token = jwtUtil.extractToken(request);


		// ✅ localStorage 대신 쿠키에서도 토큰을 추출할 수 있도록 보완
		if (token == null || token.isBlank()) {
			Cookie[] cookies = request.getCookies();
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					if ("accessToken".equals(cookie.getName())) {
						token = cookie.getValue();
						break;
					}
				}
			}
		}	// 🔍 토큰 존재 여부 확인
		log.info("🔐 추출된 accessToken: {}", token);
		if (token == null || token.isBlank()) {
			// 토큰 없으면 아무 일 없이 다음 필터로
			filterChain.doFilter(request, response);
			return;
		}

		// JWT 블랙리스트 검증
		if (redisRepository.validateKey(token)) {
			log.warn("❌ 블랙리스트 토큰 접근 시도");
			handleAuthFailure(request, response, "이미 로그아웃된 토큰입니다.");
			return;
		}//수정


		try {
			if(jwtUtil.validateToken(token)){
				// id 혹은 UserRole 검증
				UserAuth userAuth = jwtUtil.extractUserAuth(token);

				List<SimpleGrantedAuthority> authorities = List.of(
					new SimpleGrantedAuthority("ROLE_" + userAuth.getUserRole().name())
				);

				UsernamePasswordAuthenticationToken authToken =		//userAuth,null,authorities
					new UsernamePasswordAuthenticationToken(userAuth,null, authorities);

				SecurityContextHolder.getContext().setAuthentication(authToken);

				// ✅ 여기에 붙이기!
				log.info("✅ 인증 성공: userId={}, role={}", userAuth.getId(), userAuth.getUserRole());

				// ✅ 로그인한 사용자가 /login 페이지 요청하면 리다이렉트
				if (uri.equals("/login")) {
					log.info("🔁 로그인된 사용자의 /login 접근 → /home 리다이렉트");
					response.sendRedirect("/home");
					return;
			}

			}
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 예외 발생", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"유효하지 않은 접근입니다.");
			return;
		}

		filterChain.doFilter(request,response);

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
}
