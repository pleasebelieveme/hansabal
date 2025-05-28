package org.example.hansabal.domain.auth.service;

import java.util.Date;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenPair;
import org.example.hansabal.domain.auth.repository.TokenRepository;
import org.example.hansabal.domain.auth.util.JwtUtil;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.example.hansabal.domain.auth.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final TokenRepository tokenRepository;

	public TokenPair login(LoginRequest dto) {
		User user = userRepository.findByEmailOrElseThrow(dto.email());

		if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}
		return getTokenPair(user);
	}

	private TokenPair getTokenPair(User user) {
		String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());
		String refreshToken = jwtUtil.createRefreshToken(user.getId());
		String refreshTokenSubString =  jwtUtil.substringToken(refreshToken);
		tokenRepository.saveRefreshToken(String.valueOf(user.getId()), refreshTokenSubString);

		return new TokenPair(accessToken, refreshTokenSubString);
	}

	public TokenPair reissue(Long id) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));
		return getTokenPair(user);
	}
}