package org.example.hansabal.domain.users.repository;

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

	public String generateBlacklistKey(String token){
		String blacklistKey = "blacklist:" + token;

		return blacklistKey;
	}

	public boolean validateKey(String token){
		try {
			return redisTemplate.hasKey(generateBlacklistKey(token));
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	public void saveBlackListToken(String token, long expirationMillis) {
		try {
			redisTemplate.opsForValue().set(
				generateBlacklistKey(token),
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
			String key = "refresh:" + userId;
			redisTemplate.opsForValue().set(key, refreshToken, expirationMillis, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			throw new BizException(UserErrorCode.INVALID_REQUEST);
		}
	}

	public boolean validateRefreshToken(Long userId, String refreshToken) {
		String key = "refresh:" + userId;
		String savedToken = redisTemplate.opsForValue().get(key);
		return savedToken != null && savedToken.equals(refreshToken);
	}

}
