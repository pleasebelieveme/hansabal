package org.example.hansabal.domain.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.exception.AuthErrorCode;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserStatus;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final UserRepository userRepository;
	private final RedisRepository redisRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final JwtUtil jwtUtil;

	@Transactional
	public TokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmailOrElseThrow(request.email());

		if (user.getLastLoginAt() != null && user.getLastLoginAt().isBefore(LocalDateTime.now().minusYears(1))) {
			user.markAsDormant();
			userRepository.save(user);
			throw new BizException(UserErrorCode.DORMANT_ACCOUNT); // 휴면 전환 후 로그인 차단
		}

		if (user.getUserStatus() != UserStatus.ACTIVE) {
			throw new BizException(UserErrorCode.DORMANT_ACCOUNT);
		}

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}

		user.updateLastLoginTime();
		userRepository.save(user);

		TokenResponse tokenResponse = tokenService.generateTokens(user.getId(), user.getUserRole(),user.getNickname());
		tokenService.saveRefreshToken(user.getId(), tokenResponse.getRefreshToken());

		return tokenResponse;
	}

	@Transactional(readOnly = true)
	public void logout(HttpServletRequest request) {
		try {
			String token = jwtUtil.extractToken(request);

			if (token != null && jwtUtil.validateToken(token)) {
				long expiration = jwtUtil.getExpiration(token);
				redisRepository.saveBlackListToken(token, expiration);

				UserAuth userAuth = jwtUtil.extractUserAuth(token);
				redisRepository.deleteRefreshToken(userAuth.getId());
			}
		} catch (Exception e) {
			// 로그만 남기고 조용히 무시
			log.warn("로그아웃 처리 중 예외 발생: {}", e.getMessage());
		}
	}

	@Transactional(readOnly = true)
	public TokenResponse reissue(String bearerToken) {
		// 1. Bearer 제거
		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
			throw new BizException(AuthErrorCode.MISMATCHED_REFRESH_TOKEN);
		}
		String refreshToken = bearerToken.substring(7);

		// 2. 토큰 유효성 검증
		if (!jwtUtil.validateToken(refreshToken)) {
			throw new BizException(AuthErrorCode.MISMATCHED_REFRESH_TOKEN);
		}

		// 3. 유저 정보 추출
		UserAuth userAuth = jwtUtil.extractUserAuth(refreshToken);

		// 4. Redis에 저장된 Refresh Token과 일치하는지 확인
		if (!redisRepository.validateRefreshToken(userAuth.getId(), refreshToken)) {
			throw new BizException(AuthErrorCode.REUSED_REFRESH_TOKEN);
		}

		redisRepository.deleteRefreshToken(userAuth.getId());

		// 4. 새 토큰 생성 및 저장
		TokenResponse newTokens = tokenService.generateTokens(userAuth.getId(), userAuth.getUserRole(),userAuth.getNickname());
		tokenService.saveRefreshToken(userAuth.getId(), newTokens.getRefreshToken());

		return newTokens;
	}
}