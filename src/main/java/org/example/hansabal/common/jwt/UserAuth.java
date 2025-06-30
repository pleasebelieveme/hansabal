package org.example.hansabal.common.jwt;

import java.security.Principal;

import org.example.hansabal.domain.users.entity.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAuth implements Principal {
	private final Long id;
	private final UserRole userRole;
	private final String nickname;

	@Override
	public String getName() {
		return nickname;
	}
}