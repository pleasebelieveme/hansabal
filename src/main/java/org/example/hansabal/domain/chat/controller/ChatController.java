package org.example.hansabal.domain.chat.controller;

import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.chat.dto.request.ChatMessageRequest;
import org.example.hansabal.domain.chat.dto.response.ChatMessageResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

	// 클라이언트에게 메시지를 전송할 수 있는 Spring의 STOMP 메시징 도구
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 클라이언트가 "/app/dm"으로 메시지를 보내면 이 메서드가 호출됨
	 * @param message 수신자 닉네임과 메시지 내용을 포함한 DTO
	 * @param userAuth 현재 WebSocket 인증된 사용자 (보내는 사람)
	 */
	@MessageMapping("/dm")
	public void directMessage(@Payload ChatMessageRequest message,
		@AuthenticationPrincipal UserAuth userAuth) {
		// 보낸 사람 = 현재 인증된 사용자의 닉네임
		String senderId = userAuth.getNickname();

		// 수신자 닉네임 (프론트에서 보냄)
		String receiverId = message.receiverNickname();

		// 클라이언트에게 전달할 메시지 DTO 구성
		ChatMessageResponse payload = new ChatMessageResponse(senderId, receiverId, message.content());

		// 특정 유저의 개인 큐로 메시지 전송
		// → "/user/{receiverId}/queue/messages"로 전송됨
		messagingTemplate.convertAndSendToUser(
			receiverId, "/queue/messages", payload
		);
	}
}
