package org.example.hansabal.domain.chat.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.exception.ErrorResponse;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.chat.dto.request.ChatMessageRequest;
import org.example.hansabal.domain.chat.dto.response.ChatCursorSliceResponse;
import org.example.hansabal.domain.chat.dto.response.ChatMessageResponse;
import org.example.hansabal.domain.chat.dto.response.ChatCursorResponse;
import org.example.hansabal.domain.chat.exception.ChatErrorCode;
import org.example.hansabal.domain.chat.service.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	// 클라이언트에게 메시지를 전송할 수 있는 Spring의 STOMP 메시징 도구
	private final SimpMessagingTemplate messagingTemplate;

	private final ChatService chatService;

	/**
	 * 클라이언트가 "/app/dm" 경로로 STOMP 메시지를 보내면 호출되는 메서드
	 * 1:1 DM을 처리하며, 메시지를 DB에 저장하고 수신자에게 실시간으로 전달한다.
	 *
	 * @param message   수신자 닉네임과 메시지 내용을 담은 요청 객체
	 * @param principal WebSocket 세션의 인증 주체 (Handshake 또는 STOMP CONNECT에서 설정한 사용자 정보)
	 */
	@MessageMapping("/dm")
	public void directMessage(@Payload ChatMessageRequest message, Principal principal) {
		if (principal == null) {
			log.warn("❌ WebSocket 인증 실패: principal 이 null임");
			throw new BizException(ChatErrorCode.UNAUTHORIZED);
		}

		String senderId = principal.getName(); // 이제 그냥 nickname

		log.info("✅ [WebSocket] 인증된 사용자 닉네임: {}", senderId);
		log.info("수신자 [{}]에게 보냄 / 현재 Principal.getName() = {}", message.receiverNickname(), principal.getName());

		ChatMessageResponse payload = new ChatMessageResponse(
			senderId,
			message.receiverNickname(),
			message.content()
		);

		// 메시지 저장 시 senderId 기준으로 User 조회하도록 ChatService 수정 필요할 수 있음
		chatService.saveDirectMessage(message, senderId);

		messagingTemplate.convertAndSendToUser(message.receiverNickname(), "/queue/messages", payload);
		// messagingTemplate.convertAndSend("/topic/messages/" + message.receiverNickname(), payload);
	}

	// STOMP 에서 전송 중 예외 발생 시 클라이언트에게 전달되게 하는 메서드
	@MessageExceptionHandler(BizException.class)
	@SendToUser("/queue/errors")
	public ErrorResponse handleBizException(BizException e){
		return ErrorResponse.of(e.getErrorCode());
	}

	@GetMapping
	public ResponseEntity<ChatCursorSliceResponse> findChatHistory(
		@RequestParam String receiver,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
		@AuthenticationPrincipal UserAuth userAuth,
		@PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable
	){
		ChatCursorSliceResponse chatHistory = chatService.findChatHistory(receiver, userAuth, cursor,pageable);
		return ResponseEntity.status(HttpStatus.OK).body(chatHistory);
	}
}
