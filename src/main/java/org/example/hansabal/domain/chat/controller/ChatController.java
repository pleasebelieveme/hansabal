package org.example.hansabal.domain.chat.controller;

import java.security.Principal;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.exception.ErrorResponse;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.chat.dto.request.ChatMessageRequest;
import org.example.hansabal.domain.chat.dto.response.ChatMessageResponse;
import org.example.hansabal.domain.chat.dto.response.ChatMessageSimpleResponse;
import org.example.hansabal.domain.chat.exception.ChatErrorCode;
import org.example.hansabal.domain.chat.service.ChatService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

	// 클라이언트에게 메시지를 전송할 수 있는 Spring의 STOMP 메시징 도구
	private final SimpMessagingTemplate messagingTemplate;

	private final ChatService chatService;

	/**
	 * 클라이언트가 "/app/dm"으로 메시지를 보내면 이 메서드가 호출됨
	 * @param message 수신자 닉네임과 메시지 내용을 포함한 DTO
	 * @param userAuth 현재 WebSocket 인증된 사용자 (보내는 사람)
	 */
	@MessageMapping("/dm")
	public void directMessage(@Payload ChatMessageRequest message, Principal principal) {
		if(principal instanceof UsernamePasswordAuthenticationToken token &&
		token.getPrincipal() instanceof UserAuth userAuth) {

			log.info("✅ [WebSocket] 인증된 사용자 닉네임: {}", userAuth.getNickname());

			// 보낸 사람 = 현재 인증된 사용자의 닉네임
			String senderId = userAuth.getNickname();

			// 수신자 닉네임 (프론트에서 보냄)
			String receiverId = message.receiverNickname();

			// 클라이언트에게 전달할 메시지 DTO 구성
			ChatMessageResponse payload = new ChatMessageResponse(senderId, receiverId, message.content());

			// 메시지 저장
			chatService.saveDirectMessage(message, userAuth);

			// 특정 유저의 개인 큐로 메시지 전송
			// → "/user/{receiverId}/queue/messages"로 전송됨
			messagingTemplate.convertAndSendToUser(
				receiverId, "/queue/messages", payload
			);
		} else {
			log.warn("❌ WebSocket 인증 실패: principal 정보가 유효하지 않음");
			throw new BizException(ChatErrorCode.UNAUTHORIZED);
		}
	}

	// STOMP 에서 전송 중 예외 발생 시 클라이언트에게 전달되게 하는 메서드
	@MessageExceptionHandler(BizException.class)
	@SendToUser("/queue/errors")
	public ErrorResponse handleBizException(BizException e){
		return ErrorResponse.of(e.getErrorCode());
	}

	@GetMapping
	public ResponseEntity<Slice<ChatMessageSimpleResponse>> findChatHistory(
		@RequestParam String receiver,
		@AuthenticationPrincipal UserAuth userAuth,
		@PageableDefault(size = 20, sort = "sentAt", direction = Sort.Direction.DESC) Pageable pageable
	){
		Slice<ChatMessageSimpleResponse> chatHistory = chatService.findChatHistory(receiver, userAuth, pageable);
		return ResponseEntity.status(HttpStatus.OK).body(chatHistory);
	}
}
