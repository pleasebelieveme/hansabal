package org.example.hansabal.domain.chat.dto.response;

public record ChatMessageResponse(
	String senderNickname,
	String receiverNickname,
	String content
) {
}
