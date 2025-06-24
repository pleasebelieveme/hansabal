package org.example.hansabal.domain.chat.dto.response;

import java.time.LocalDateTime;

public record ChatCursorResponse(
	String senderNickname,
	String content,
	LocalDateTime sentAt
) {
}
