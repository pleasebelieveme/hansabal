package org.example.hansabal.domain.chat.service;

import java.util.List;

import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.common.jwt.UserAuth;
import org.example.hansabal.domain.chat.dto.request.ChatMessageRequest;
import org.example.hansabal.domain.chat.entity.Chat;
import org.example.hansabal.domain.chat.exception.ChatErrorCode;
import org.example.hansabal.domain.chat.repository.ChatRepository;
import org.example.hansabal.domain.users.entity.User;
import org.example.hansabal.domain.users.repository.UserRepository;
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
}
