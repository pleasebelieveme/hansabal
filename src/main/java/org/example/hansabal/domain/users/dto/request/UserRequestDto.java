package org.example.hansabal.domain.users.dto.request;

import org.example.hansabal.domain.users.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserRequestDto {

	private final String email;
	private final String password;
	private final String name;
	private final String nickname;
	private final UserRole userRole;

}
