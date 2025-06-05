package org.example.hansabal.common.oauth2;

import java.io.IOException;

import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final UserRepository	userRepository;
	private final RedisRepository redisRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException, ServletException {

		// DefaultOAuth2User에서 email 추출
		String email = authentication.getName(); // "email"로 설정했었지

		User user = userRepository.findByEmailOrElseThrow(email);

		// JWT 토큰 생성
		String accessToken = jwtUtil.createToken(user.getId(), user.getUserRole());
		String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUserRole());

		// 필요시 RefreshToken 저장 (ex: Redis 등)
		redisRepository.saveRefreshToken(user.getId(), refreshToken, jwtUtil.getRefreshExpiration(refreshToken));

		// 응답 바디 설정
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// TokenResponse 객체 → JSON 변환 후 응답
		TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);
		objectMapper.writeValue(response.getWriter(), tokenResponse);

		log.info("소셜 로그인 성공 - email: {}", email);
	}
}
