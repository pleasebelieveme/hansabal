package org.example.hansabal.domain.users.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdateRequestDto {

	private final String nickname;
	private final String oldPassword;
	private final String newPassword;
}
