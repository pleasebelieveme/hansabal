package org.example.hansabal.domain.chat.service;

import java.time.LocalDateTime;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.chat.dto.request.ChatMessageRequest;
import org.example.hansabal.domain.chat.dto.response.ChatCursorResponse;
import org.example.hansabal.domain.chat.dto.response.ChatCursorSliceResponse;
import org.example.hansabal.domain.chat.entity.Chat;
import org.example.hansabal.domain.chat.exception.ChatErrorCode;
import org.example.hansabal.domain.chat.repository.ChatRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

	private final ChatRepository chatRepository;
	private final UserRepository userRepository;

	@Transactional
	public void saveDirectMessage(ChatMessageRequest request, String senderId){

		User receiver = userRepository.findByNickname(request.receiverNickname()).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		User sender = userRepository.findByNickname(senderId).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		Chat chat = new Chat(sender,receiver,request.content());

		chatRepository.save(chat);
	}

	@Cacheable(
		value = "chatHistoryCache", // 캐시의 이름 (cacheName) Redis의 Key Prefix로 사용됨
		key = "#receiver + ':' + #userAuth.nickname + ':' + #cursor", // 파라미터 값을 조합한 캐싱용 키(Pagable은 캐시 매핑이 불가능)
		unless = "#result == null || #result.content().isEmpty()" // 캐시를 저장하지 않을 조건
	)
	@Transactional(readOnly = true)
	public ChatCursorSliceResponse findChatHistory(String receiver,UserAuth userAuth, LocalDateTime cursor,Pageable pageable) {
		User sender = userRepository.findByNickname(userAuth.getNickname()).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		User receiveUser = userRepository.findByNickname(receiver).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		return  chatRepository.findAllBySenderAndReceiver(sender.getId(),receiveUser.getId(),cursor,pageable);
	}
}
