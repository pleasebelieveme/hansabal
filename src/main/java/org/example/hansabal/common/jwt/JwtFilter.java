package org.example.hansabal.common.jwt;

import java.io.IOException;
import java.util.List;

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

		String token = jwtUtil.extractToken(request);

		// JWT 블랙리스트 검증
		if(redisRepository.validateKey(token)){
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"이미 로그아웃된 아이디입니다.");
			return;
		}

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
			}
		} catch (Exception e) {
			log.error("JWT 인증 처리 중 예외 발생", e);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"유효하지 않은 접근입니다.");
			return;
		}

		filterChain.doFilter(request,response);
	}
}
