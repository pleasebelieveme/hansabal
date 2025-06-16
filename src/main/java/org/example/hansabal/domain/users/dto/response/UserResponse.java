package org.example.hansabal.domain.users.dto.response;

import org.example.hansabal.domain.users.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponse(

		@Schema(description = "사용자 ID", example = "1")
		Long id,

		@Schema(description = "사용자 이메일", example = "user@example.com")
		String email,

		@Schema(description = "사용자 이름", example = "홍길동")
		String name,

		@Schema(description = "사용자 닉네임", example = "길동이")
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
