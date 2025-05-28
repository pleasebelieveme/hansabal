package org.example.hansabal.common.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

	private final Object principal;
	private final Object credentials;
	private final String email;
	private final String nickname;

	public CustomAuthenticationToken(Object principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities,
		String email, String nickname) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		this.email = email;
		this.nickname = nickname;
		super.setAuthenticated(true); // 인증 완료로 설정
	}

	public CustomAuthenticationToken(Object principal, Object credentials,
		Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		this.credentials = credentials;
		this.email = null;
		this.nickname = null;
		super.setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return credentials;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	public String getEmail() {
		return email;
	}

	public String getNickname() {
		return nickname;
	}
}