package org.example.hansabal.domain.users.repository;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.email.exception.EmailErrorCode;
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
			System.out.println("저장 시도 key: " + key);
			System.out.println("refreshToken: " + refreshToken);
			System.out.println("expirationMillis: " + expirationMillis);
			System.out.println("redisTemplate: " + redisTemplate);

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

	public void saveEmailAuthCode(String email, String code, Duration ttl) {
		redisTemplate.opsForValue().set("EMAIL_CODE:" + email, code, ttl);
	}

	public String getEmailAuthCode(String email) {
		String code = redisTemplate.opsForValue().get("EMAIL_CODE:" + email);
		if (code == null) {
			throw new BizException(EmailErrorCode.EMAIL_CODE_NOT_FOUND);
		}
		return code;
	}

	public void deleteEmailAuthCode(String email) {
		redisTemplate.delete("EMAIL_CODE:" + email);
	}

	public void save(String key, String value, Duration ttl) {
		try {
			redisTemplate.opsForValue().set(key, value, ttl);
		} catch (Exception e) {
			throw new BizException(EmailErrorCode.SEND_FAILED);
		}
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public void delete(String key) {
		redisTemplate.delete(key);
	}
}
