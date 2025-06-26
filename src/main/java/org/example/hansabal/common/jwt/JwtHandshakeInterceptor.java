package org.example.hansabal.common.jwt;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

	private final JwtUtil jwtUtil;

	/**
	 * WebSocket 연결 요청이 들어올 때 최초 1회 실행됨
	 * JWT를 검증하고 SecurityContext에 인증 정보를 등록함
	 * JwtFilter와 유사한 형태를 띄고 있지만 JwtFilter는 HTTP 요청마다 적용되는 반면,
	 * HandshakeInterceptor 는 WebSocket 연결 시에만 작동됨
	 * 적용 위치와 용도, 실행 시점등이 상이하며 HttpServlet chain과 별개로 동작하기 때문에
	 * 따로 구현해야 함
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Map<String, Object> attributes) throws Exception {

		String token = extractTokenFromRequest(request);
		if (token == null || !jwtUtil.validateToken(token)){
			log.warn("WebSocket 인증 실패: 토큰이 유효하지 않음");
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}

		UserAuth userAuth = jwtUtil.extractUserAuth(token);

		List<GrantedAuthority> authorities = List.of(
			new SimpleGrantedAuthority("ROLE_" + userAuth.getUserRole().name())
		);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userAuth.getName(),null,authorities
		);

		log.info("WebSocket 인증 완료: principal = {}, getName = {}", authentication.getPrincipal(), authentication.getName());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
		Exception exception) {

	}

	private String extractTokenFromRequest(ServerHttpRequest request){
		String query = request.getURI().getQuery();
		if(query == null || !query.startsWith("token=")){
			return null;
		}

		return query.substring("token=".length());
	}
}
