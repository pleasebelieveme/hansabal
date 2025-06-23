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

	/**
	 * STOMP 메시지 처리 채널에서 JWT 인증을 수행하는 인터셉터
	 * WebSocket 연결 후, 클라이언트가 보낸 STOMP CONNECT 프레임을 가로채고
	 * JWT 토큰을 검증하여 인증 정보를 설정함.
	 */

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		// STOMP 메시지의 헤더 정보를 파싱하기 위한 액세서 생성
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		// STOMP 연결 요청 (CONNECT 명령)일 경우만 인증 처리 수행
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			// STOMP 헤더 중 Authorization 값을 가져옴 (ex. Bearer eyJ...)
			String token = accessor.getFirstNativeHeader("Authorization");
			// 토큰이 없거나 형식이 "Bearer ..."가 아닌 경우, 인증 처리하지 않고 그대로 진행
			if (token == null || !token.startsWith("Bearer ")) {
				return message;
			}

			token = token.substring(7);

			// 토큰 유효성 검증 실패 시, 인증 처리하지 않고 그대로 진행
			if (!jwtUtil.validateToken(token)) {
				return message;
			}

			// 토큰이 유효하면 사용자 인증 정보(UserAuth)를 추출
			UserAuth userAuth = jwtUtil.extractUserAuth(token);

			// 추출한 사용자 정보를 기반으로 스프링 Security의 Authentication 객체 생성
			Authentication authentication = new UsernamePasswordAuthenticationToken(
				userAuth, null,
				List.of(new SimpleGrantedAuthority("ROLE_" + userAuth.getUserRole()))
			);

			// WebSocket 세션에 인증 정보 주입 (principal로 사용됨)
			// SecurityContextHolder에도 인증 정보 설정
			accessor.setUser(authentication);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		// 인증 처리를 마친 메시지를 그대로 반환 (또는 아무 변경도 안 했으면 그대로 전달)
		return message;
	}
}
