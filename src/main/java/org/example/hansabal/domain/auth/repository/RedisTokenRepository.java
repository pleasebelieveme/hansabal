package org.example.hansabal.domain.auth.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisTokenRepository implements TokenRepository{

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration;

	private final RedisTemplate<String, Object> redisTemplate;

	@Override
	public void saveRefreshToken(String id, String refreshToken) {
		redisTemplate.opsForValue().set(
			"userId:" + id,
			"refreshToken:" + refreshToken,
			Duration.ofMillis(refreshTokenExpiration));
	}

	@Override
	public boolean isRefreshTokenValid(String id, String refreshToken) {
		String key = "userId:" + id;
		Object storedToken = redisTemplate.opsForValue().get(key);

		return ("refreshToken:" + refreshToken).equals(String.valueOf(storedToken));
	}

	@Override
	public void deleteRefreshToken(String id) {
		redisTemplate.delete("userId:" + id);
	}
}