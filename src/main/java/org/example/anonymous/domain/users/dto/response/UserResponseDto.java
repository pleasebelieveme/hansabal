package org.example.anonymous.domain.users.dto.response;

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
}
