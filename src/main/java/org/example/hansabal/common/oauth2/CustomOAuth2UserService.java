package org.example.hansabal.common.oauth2;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.entity.UserRole;
import org.example.hansabal.domain.users.exception.UserErrorCode;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oAuth2User.getAttributes();

		log.info("OAuth2 [{}] attributes: {}", registrationId, attributes);

		Map<String, Object> userInfo;

		if ("naver".equals(registrationId)) {
			userInfo = (Map<String, Object>) attributes.get("response");
		} else if ("kakao".equals(registrationId)) {
			Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
			Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

			userInfo = Map.of(
				"email", kakaoAccount.get("email"),
				"name", profile.get("nickname")
			);
		} else {
			userInfo = attributes;
		}

		String email = (String) userInfo.get("email");
		String name = (String) userInfo.get("name");

		if (email == null) {
			throw new IllegalArgumentException("OAuth2 로그인에 이메일 정보가 없습니다.");
		}

		User user = userRepository.findByEmail(email)
			.orElseGet(() -> userRepository.save(User.builder()
				.email(email)
				.password("")
				.name(name)
				.nickname(generateUniqueNickname(name))
				.userRole(UserRole.USER)
				.build()
			));

		Map<String, Object> customAttributes = Map.of(
			"email", email,
			"name", name,
			"registrationId", registrationId
		);

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getUserRole())),
			customAttributes,
			"email"
		);
	}

	private String generateUniqueNickname(String name) {
		String nickname;
		int tryCount = 0;
		final int MAX_TRY = 5;

		do {
			nickname = name + "_" + UUID.randomUUID().toString().substring(0, 8);
			tryCount++;
		} while (userRepository.existsByNickname(nickname) && tryCount < MAX_TRY);

		if (userRepository.existsByNickname(nickname)) {
			throw new BizException(UserErrorCode.DUPLICATED_NICKNAME);
		}

		return nickname;
	}
}