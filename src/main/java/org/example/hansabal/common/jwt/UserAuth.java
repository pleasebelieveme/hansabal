package org.example.hansabal.common.jwt;

import org.example.hansabal.domain.users.entity.UserRole;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAuth {
	private final Long id;
	private final UserRole userRole;
}