package org.example.hansabal.common.config;

import java.util.List;

import org.example.hansabal.common.jwt.JwtUtil;
import org.example.hansabal.common.jwt.UserAuth;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

	private final JwtUtil jwtUtil;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			String token = accessor.getFirstNativeHeader("Authorization");
			if (token == null || !token.startsWith("Bearer ")) {
				return message;
			}

			token = token.substring(7);
			if (!jwtUtil.validateToken(token)) {
				return message;
			}

			UserAuth userAuth = jwtUtil.extractUserAuth(token);

			Authentication authentication = new UsernamePasswordAuthenticationToken(
				userAuth, null,
				List.of(new SimpleGrantedAuthority("ROLE_" + userAuth.getUserRole()))
			);

			// STOMP 세션 속성에 인증 정보 저장
			accessor.setUser(authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication); // optional
		}

		return message;
	}
}
