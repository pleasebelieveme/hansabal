package org.example.hansabal.domain.users.dto.response;

import org.example.hansabal.domain.users.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {

	private Long id;
	private final String email;
	private final String name;
	private final String nickname;

	public static UserResponseDto toDto(User user) {
		return UserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.nickname(user.getNickname())
			.build();
	}
}
