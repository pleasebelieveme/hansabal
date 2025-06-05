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

		Map<String, Object> attributes = oAuth2User.getAttributes();

		log.info("OAuth2 attributes: {}", attributes);

		// 구글의 경우 기본 필드
		String email = (String) attributes.get("email");
		String name = (String) attributes.get("name");

		// 이메일 기반으로 유저 조회 or 신규 생성
		User user = userRepository.findByEmail(email)
			.orElseGet(() -> {
				return userRepository.save(User.builder()
					.email(email)
					.password("") // 비밀번호는 OAuth2 로그인 시 사용 안 하므로 빈 문자열
					.name(name)
					.nickname(generateUniqueNickname(name)) // 중복 피하기 위한 처리
					.userRole(UserRole.USER)
					.build());
			});

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getUserRole())),
			attributes,
			"email" // OAuth2User.getName() 시 반환될 키
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