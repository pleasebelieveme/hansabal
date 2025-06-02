package org.example.hansabal.domain.users.repository;

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
}
