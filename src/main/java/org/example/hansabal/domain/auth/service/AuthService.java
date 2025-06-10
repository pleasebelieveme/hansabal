package org.example.hansabal.domain.auth.service;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.auth.dto.request.LoginRequest;
import org.example.hansabal.domain.auth.dto.response.TokenResponse;
import org.example.hansabal.domain.auth.exception.AuthErrorCode;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.RedisRepository;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@Transactional // AuthService의 모든 public 메서드에만 트랜잭션 적용
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final RedisRepository redisRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final JwtUtil jwtUtil;

	public TokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmailOrElseThrow(request.email());

		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}

		return tokenService.createTokens(user.getId(), user.getUserRole());
	}

	public void logout(HttpServletRequest request) {
		String token = jwtUtil.extractToken(request);

		// 토큰이 유효한 경우만 블랙리스트에 등록
		// 등록된 블랙리스트는 30분 후 제거 -> 엑세스토큰과 시간을 맞추는 것이 일반적
		if (token != null && jwtUtil.validateToken(token)) {
			long expiration = jwtUtil.getExpiration(token);
			redisRepository.saveBlackListToken(token, expiration);
		}

		// 유저 ID 추출 후 리프레시 토큰 제거
		UserAuth userAuth = jwtUtil.extractUserAuth(token);
		redisRepository.deleteRefreshToken(userAuth.getId());
	}

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

		return tokenService.createTokens(userAuth.getId(), userAuth.getUserRole());
	}
}