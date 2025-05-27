package org.example.anonymous.domain.auth.service;

import java.util.Date;

import org.example.anonymous.domain.auth.dto.UserAuth;
import org.example.anonymous.domain.auth.dto.request.LoginRequest;
import org.example.anonymous.domain.auth.dto.response.TokenResponse;
import org.example.anonymous.domain.users.entity.User;
import org.example.anonymous.domain.users.repository.UserRepository;
import org.example.anonymous.jwt.service.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public TokenResponse signUp(LoginRequest dto) throws Exception {

		User user = userRepository.findByEmailOrElseThrow(dto.email());

		// checkPassword(dto.password(), user.getPassword());

		return jwtService.generateToken(UserAuth.from(user), new Date());
	}

	private void checkPassword(String rawPassword, String hashedPassword) throws Exception {

		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new Exception("비밀번호가 일치하지 않습니다.");
		}

	}

}
