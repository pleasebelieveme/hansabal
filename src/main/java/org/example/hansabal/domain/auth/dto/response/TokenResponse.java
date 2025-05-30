package org.example.hansabal.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class TokenResponse {
	private final String accessToken;

	public TokenResponse(String accessToken) {
		this.accessToken = "Bearer " + accessToken;
	}
}
