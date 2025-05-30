package org.example.hansabal.domain.auth.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.domain.auth.dto.request.LoginRequestDto;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional // AuthService의 모든 public 메서드에만 트랜잭션 적용
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	public TokenResponse login(LoginRequestDto request) {
		User user = userRepository.findByEmailOrElseThrow(request.email());

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}

		String accessToken = jwtUtil.createToken(user.getId(), user.getUserRole());
		return new TokenResponse(accessToken);
	}
}