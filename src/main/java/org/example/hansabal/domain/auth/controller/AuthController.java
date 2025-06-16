package org.example.hansabal.domain.auth.controller;

import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "AuthController", description = "인증(Auth) 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	@Operation(
			summary = "로그인",
			description = "이메일과 비밀번호를 입력받아 로그인하고, AccessToken과 RefreshToken을 반환합니다."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "로그인 성공"),
			@ApiResponse(responseCode = "401", description = "인증 실패")
	})
	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		TokenResponse tokenResponse = authService.login(request);
		return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
	}

	@Operation(
			summary = "로그아웃",
			description = "현재 로그인된 사용자를 로그아웃 처리합니다. AccessToken을 무효화합니다."
	)
	@ApiResponse(responseCode = "200", description = "로그아웃 성공")
	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		authService.logout(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(
			summary = "토큰 재발급",
			description = "RefreshToken을 사용하여 새로운 AccessToken과 RefreshToken을 발급받습니다."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "재발급 성공"),
			@ApiResponse(responseCode = "401", description = "RefreshToken 인증 실패")
	})
	@PostMapping("/reissue")
	public ResponseEntity<TokenResponse> reissue(@RequestHeader("Authorization") String refreshToken) {
		TokenResponse tokenResponse = authService.reissue(refreshToken);
		return ResponseEntity.ok(tokenResponse);
	}
}
