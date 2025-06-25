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

		TokenResponse tokenResponse = tokenService.generateTokens(
				user.getId(),
				user.getUserRole(),
				user.getNickname()
		);
		tokenService.saveRefreshToken(user.getId(), tokenResponse.getRefreshToken());

		log.info("소셜 로그인 성공 - email: {}", email);

		// 요청 헤더로 API 여부 확인 (ex: XMLHttpRequest 또는 fetch)
		String accept = request.getHeader("Accept");
		String xhr = request.getHeader("X-Requested-With");

		boolean isApiRequest =
				(accept != null && accept.contains("application/json")) ||
						(xhr != null && xhr.equalsIgnoreCase("XMLHttpRequest"));

		if (isApiRequest) {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			objectMapper.writeValue(response.getWriter(), tokenResponse);
			return;
		}

		// ✅ Redirect URI 설정 (없으면 /home)
		String redirectUri = (String) request.getSession().getAttribute("redirect_uri");
		if (redirectUri == null || redirectUri.isBlank()) {
			redirectUri = "/home";
		}
		request.getSession().removeAttribute("redirect_uri");

		String tokenJson = objectMapper.writeValueAsString(tokenResponse);
		String html = """
<!DOCTYPE html>
<html lang="ko">
<head><meta charset="UTF-8"><title>로그인 성공</title></head>
<body>
<script>
    const token = %s;
    localStorage.setItem("accessToken", token.accessToken);
    localStorage.setItem("refreshToken", token.refreshToken);
    document.cookie = `accessToken=${token.accessToken}; path=/; max-age=1800;`;

    setTimeout(() => {
        window.location.href = "%s";
    }, 200);
</script>
</body>
</html>
    """.formatted(tokenJson, redirectUri);

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(html);


	}
}
