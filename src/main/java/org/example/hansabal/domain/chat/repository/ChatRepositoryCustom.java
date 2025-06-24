package org.example.hansabal.domain.chat.repository;

import java.time.LocalDateTime;

import org.example.hansabal.domain.chat.dto.response.ChatCursorResponse;
import org.example.hansabal.domain.chat.dto.response.ChatCursorSliceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatRepositoryCustom {
	ChatCursorSliceResponse findAllBySenderAndReceiver(Long senderId, Long receiverId, LocalDateTime cursor,Pageable pageable);
}
