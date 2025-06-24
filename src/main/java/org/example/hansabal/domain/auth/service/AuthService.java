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

/**
 * 인증 관련 비즈니스 로직을 담당하는 서비스 클래스입니다.
 * - 로그인
 * - 로그아웃
 * - 리프레시 토큰 재발급
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

	private final UserRepository userRepository;
	private final RedisRepository redisRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;
	private final JwtUtil jwtUtil;

	/**
	 * 사용자 로그인 처리 메서드입니다.
	 * <p>
	 * - 이메일로 사용자를 조회하고<br>
	 * - 휴면 계정인지 확인하며, 1년 이상 미접속 시 자동으로 휴면 전환합니다.<br>
	 * - 비밀번호를 비교하여 유효성 검증 후<br>
	 * - 마지막 로그인 시간을 갱신하고<br>
	 * - JWT Access/Refresh 토큰을 생성하여 응답합니다.
	 * </p>
	 *
	 * @param request 로그인 요청 DTO (이메일, 비밀번호 포함)
	 * @return Access/Refresh 토큰이 포함된 응답 객체
	 * @throws BizException 휴면 계정이거나, 비밀번호가 일치하지 않는 경우 예외 발생
	 */
	@Transactional
	public TokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmailOrElseThrow(request.email());

		if (user.getLastLoginAt() != null && user.getLastLoginAt().isBefore(LocalDateTime.now().minusYears(1))) {
			user.markAsDormant();
			userRepository.save(user);
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

	/**
	 * 사용자의 로그아웃 처리 메서드입니다.
	 * <p>
	 * - 요청의 JWT 토큰을 추출하고<br>
	 * - 해당 토큰을 블랙리스트에 등록하며<br>
	 * - Redis에 저장된 Refresh Token을 삭제합니다.
	 * </p>
	 *
	 * @param request HTTP 요청 객체 (헤더에 Authorization 포함됨)
	 */
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

	/**
	 * 리프레시 토큰 기반의 Access/Refresh 토큰 재발급 메서드입니다.
	 * <p>
	 * - Bearer 접두사 제거 후<br>
	 * - 토큰의 유효성 및 서명 검증<br>
	 * - Redis에 저장된 Refresh 토큰과 비교 후<br>
	 * - 재사용 방지를 위해 기존 토큰 삭제<br>
	 * - 새 토큰 생성 및 저장
	 * </p>
	 *
	 * @param bearerToken "Bearer {token}" 형식의 Authorization 헤더
	 * @return 새로 발급된 토큰 정보
	 * @throws BizException 헤더 형식 오류, 토큰 만료/위조/불일치 시 예외 발생
	 */
	@Transactional(readOnly = true)
	public TokenResponse reissue(String bearerToken) {

		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
			throw new BizException(AuthErrorCode.INVALID_AUTH_HEADER);
		}
		String refreshToken = bearerToken.substring(7);

		if (!jwtUtil.validateToken(refreshToken)) {
			throw new BizException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		UserAuth userAuth = jwtUtil.extractUserAuth(refreshToken);

		if (!redisRepository.validateRefreshToken(userAuth.getId(), refreshToken)) {
			throw new BizException(AuthErrorCode.REUSED_REFRESH_TOKEN);
		}

		redisRepository.deleteRefreshToken(userAuth.getId());

		TokenResponse newTokens = tokenService.generateTokens(userAuth.getId(), userAuth.getUserRole(),userAuth.getNickname());
		tokenService.saveRefreshToken(userAuth.getId(), newTokens.getRefreshToken());

		return newTokens;
	}
}