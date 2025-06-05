package org.example.hansabal.domain.users.repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

	private final RedisTemplate<String,String> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "refresh:";
	private static final String BLACKLIST_PREFIX = "blacklist:";

	public boolean validateKey(String token){
		try {
			return redisTemplate.hasKey(BLACKLIST_PREFIX + token);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	public void saveBlackListToken(String token, long expirationMillis) {
		try {
			redisTemplate.opsForValue().set(
				BLACKLIST_PREFIX + token,
				"logout",
				expirationMillis,
				java.util.concurrent.TimeUnit.MILLISECONDS
			);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	public void saveRefreshToken(Long userId, String refreshToken, long expirationMillis) {
		try {
			String key = REFRESH_TOKEN_PREFIX + userId;
			redisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	public boolean validateRefreshToken(Long userId, String refreshToken) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		String savedToken = redisTemplate.opsForValue().get(key);
		return Objects.equals(savedToken, refreshToken);
	}

	public void deleteRefreshToken(Long userId) {
		String key = REFRESH_TOKEN_PREFIX + userId;
		redisTemplate.delete(key);
	}
}
