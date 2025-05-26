package org.example.anonymous.domain.auth.controller;

import org.example.anonymous.domain.auth.dto.request.LoginRequest;
import org.example.anonymous.domain.auth.dto.response.TokenResponse;
import org.example.anonymous.domain.auth.service.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	public final AuthService authService;

	@PostMapping("signup")
	public TokenResponse signUp(@RequestBody LoginRequest dto) throws Exception {
		return authService.signUp(dto);
	}
}
