package org.example.hansabal.domain.auth.repository;

public interface TokenRepository {
	void saveRefreshToken(String id, String refreshToken);
	boolean isRefreshTokenValid(String id, String refreshToken);
	void deleteRefreshToken(String id);
}
