package org.example.hansabal.common.config;

import org.example.hansabal.common.jwt.JwtHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Configuration
@EnableWebSocketMessageBroker // Stomp 기반 메시지 브로커 활성화
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
	private final AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

	/**
	 * 클라이언트가 최초로 WebSocket 연결을 시도할 endpoint 를 설정함
	 * 예: /ws → SockJS 사용 가능
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws") // WebSocket 연결 주소
			.setAllowedOriginPatterns("*") // CORS 허용 (프론트 도메인에 따라 제한 권장)
			.addInterceptors(jwtHandshakeInterceptor) // JWT 인증 인터셉터 등록
			.withSockJS(); // SockJS fallback 지원 (WebSocket 미지원 브라우저 대응)

		registry.addEndpoint("/connection")
			.setAllowedOriginPatterns("*")
			.addInterceptors(jwtHandshakeInterceptor);

	}

	/**
	 * 메시지 브로커 설정 (STOMP 내부 처리 경로 정의)
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 서버 → 클라이언트 응답 시 사용할 경로 prefix
		registry.enableSimpleBroker("/user");

		// 클라이언트 → 서버 전송 시 prefix
		registry.setApplicationDestinationPrefixes("/app");

		// convertAndSendToUser(...) 호출 시 내부적으로 붙는 prefix
		registry.setUserDestinationPrefix("/user");
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(authChannelInterceptorAdapter); // @Autowired 된 Interceptor
	}

	@PostConstruct
	public void init() {
		log.info("WebSocketConfig 적용됨");
	}
}
