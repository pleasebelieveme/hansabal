package org.example.hansabal.domain.chat.dto.response;

public record ChatMessageResponse(
	String senderId,
	String receiverId,
	String content
) {
}
