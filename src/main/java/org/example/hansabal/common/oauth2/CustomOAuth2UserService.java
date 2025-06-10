package org.example.hansabal.common.oauth2;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.auth.exception.AuthErrorCode;
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
	private final OAuth2UserInfoExtractor extractor;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		Map<String, Object> attributes = oAuth2User.getAttributes();

		log.info("OAuth2 [{}] attributes: {}", registrationId, attributes);

		OAuth2UserInfo userInfo = extractor.extract(registrationId, attributes);

		String email = userInfo.email();
		String name = userInfo.name();

		if (email == null) {
			throw new BizException(AuthErrorCode.OAUTH2_EMAIL_NOT_FOUND);
		}

		User user = userRepository.findByEmail(email)
			.orElseGet(() -> userRepository.save(User.builder()
				.email(email)
				.password("") // OAuth는 비밀번호 필요 없음
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
		final int MAX_TRY = 5;

		for (int i = 0; i < MAX_TRY; i++) {
			String nickname = String.format("%s_%s", name, UUID.randomUUID().toString().substring(0, 8));
			if (!userRepository.existsByNickname(nickname)) {
				return nickname;
			}
		}

		throw new BizException(UserErrorCode.DUPLICATED_NICKNAME);
	}
}


