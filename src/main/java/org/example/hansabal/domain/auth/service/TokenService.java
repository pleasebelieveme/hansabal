package org.example.hansabal.domain.auth.service;

import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private final JwtUtil jwtUtil;
	private final RedisRepository redisRepository;

	public TokenResponse createTokens(Long userId, UserRole userRole) {
		String accessToken = jwtUtil.createToken(userId, userRole);
		String refreshToken = jwtUtil.createRefreshToken(userId, userRole);

		long refreshExpiration = jwtUtil.getRefreshExpiration(refreshToken);
		redisRepository.saveRefreshToken(userId, refreshToken, refreshExpiration);

		return new TokenResponse(accessToken, refreshToken);
	}
}
