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
	public void saveDirectMessage(ChatMessageRequest request, UserAuth userAuth){

		User receiver = userRepository.findByNickname(request.receiverNickname()).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		User sender = userRepository.findByNickname(userAuth.getNickname()).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		Chat chat = new Chat(sender,receiver,request.content());

		chatRepository.save(chat);
	}

	@Transactional(readOnly = true)
	public ChatCursorSliceResponse findChatHistory(String receiver,UserAuth userAuth, LocalDateTime cursor,Pageable pageable) {
		User sender = userRepository.findByNickname(userAuth.getNickname()).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		User receiveUser = userRepository.findByNickname(receiver).orElseThrow(
			() -> new BizException(ChatErrorCode.INVALID_NICKNAME));

		return  chatRepository.findAllBySenderAndReceiver(sender.getId(),receiveUser.getId(),cursor,pageable);
	}
}
