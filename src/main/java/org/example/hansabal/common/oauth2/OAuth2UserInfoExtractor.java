package org.example.hansabal.common.oauth2;

import java.util.Map;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.auth.exception.AuthErrorCode;
import org.springframework.stereotype.Component;

@Component
public class OAuth2UserInfoExtractor {

	public OAuth2UserInfo extract(String registrationId, Map<String, Object> attributes) {
		return switch (registrationId) {
			case "kakao" -> extractFromKakao(attributes);
			case "naver" -> extractFromNaver(attributes);
			case "google" -> extractFromGoogle(attributes);
			default -> throw new BizException(AuthErrorCode.OAUTH2_PROVIDER_NOT_SUPPORTED);
		};
	}

	// http://localhost:8080/oauth2/authorization/kakao
	private OAuth2UserInfo extractFromKakao(Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
		return new OAuth2UserInfo(
			(String) kakaoAccount.get("email"),
			(String) profile.get("nickname")
		);
	}

	// http://localhost:8080/oauth2/authorization/naver
	private OAuth2UserInfo extractFromNaver(Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");
		return new OAuth2UserInfo(
			(String) response.get("email"),
			(String) response.get("name")
		);
	}

	// http://localhost:8080/oauth2/authorization/google
	private OAuth2UserInfo extractFromGoogle(Map<String, Object> attributes) {
		return new OAuth2UserInfo(
			(String) attributes.get("email"),
			(String) attributes.get("name")
		);
	}
}
