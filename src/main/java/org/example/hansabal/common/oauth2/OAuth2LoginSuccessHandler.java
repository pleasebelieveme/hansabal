package org.example.hansabal.common.oauth2;

import java.io.IOException;

import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.service.TokenService;
import org.example.hansabal.domain.users.entity.User;
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

	private final UserRepository	userRepository;
	private final TokenService tokenService;
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

		TokenResponse tokenResponse = tokenService.generateTokens(user.getId(), user.getUserRole());
		tokenService.saveRefreshToken(user.getId(), tokenResponse.getRefreshToken());

		// 응답 바디 설정 및 JSON 변환
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), tokenResponse);

		log.info("소셜 로그인 성공 - email: {}", email);
	}
}
