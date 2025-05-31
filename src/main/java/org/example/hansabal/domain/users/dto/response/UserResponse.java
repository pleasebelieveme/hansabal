package org.example.hansabal.domain.users.dto.response;

import org.example.hansabal.domain.users.entity.User;

public record UserResponse(
	Long id,
	String email,
	String name,
	String nickname
) {
	public static UserResponse from(User user) {
		return new UserResponse(
			user.getId(),
			user.getEmail(),
			user.getName(),
			user.getNickname()
		);
	}
}