package org.example.anonymous.domain.users.dto.request;

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

}
