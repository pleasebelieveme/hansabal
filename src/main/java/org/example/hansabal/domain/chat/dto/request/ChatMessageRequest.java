package org.example.hansabal.domain.chat.dto.request;

public record ChatMessageRequest(
	String receiverNickname,
	String content
) {
}
