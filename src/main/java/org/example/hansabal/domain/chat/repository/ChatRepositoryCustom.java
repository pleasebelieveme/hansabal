package org.example.hansabal.domain.chat.repository;

import org.example.hansabal.domain.chat.dto.response.ChatMessageSimpleResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatRepositoryCustom {
	Slice<ChatMessageSimpleResponse> findAllBySenderAndReceiver(Long senderId, Long receiverId, Pageable pageable);
}
