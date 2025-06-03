package org.example.hansabal.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class TokenResponse {
	private final String accessToken;
	private final String refreshToken;

	public TokenResponse(String accessToken, String refreshToken) {
		this.accessToken = "Bearer " + accessToken;
		this.refreshToken = "Bearer " + refreshToken;
	}
}
