package org.example.hansabal.domain.auth.controller;

import org.example.hansabal.common.security.CurrentUser;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.ReissueResponse;
import org.example.hansabal.domain.auth.dto.response.SignInResponse;
import org.example.hansabal.domain.auth.dto.response.TokenPair;
import org.example.hansabal.domain.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@Value("${jwt.refresh-token-expiration}")
	private int refreshTokenExpiration;

	@PostMapping("/api/auth/login")
	public ResponseEntity<SignInResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
		TokenPair tokenPair = authService.login(loginRequest);
		Cookie cookie = new Cookie("refreshToken", tokenPair.refreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(refreshTokenExpiration);
		response.addCookie(cookie);
		return ResponseEntity.ok(new SignInResponse(tokenPair.accessToken()));
	}

	@PostMapping("/api/auth/reissue")
	public ResponseEntity<ReissueResponse> reissue(@CurrentUser Long id, HttpServletResponse httpResponse) {
		TokenPair tokenPair = authService.reissue(id);
		Cookie cookie = new Cookie("refreshToken", tokenPair.refreshToken());
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(refreshTokenExpiration);
		httpResponse.addCookie(cookie);
		return ResponseEntity.ok(new ReissueResponse(tokenPair.accessToken()));
	}

}
