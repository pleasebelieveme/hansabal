package org.example.hansabal.domain.chat.repository;

import java.util.List;

import org.example.hansabal.domain.chat.dto.response.ChatMessageSimpleResponse;
import org.example.hansabal.domain.chat.entity.QChat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Slice<ChatMessageSimpleResponse> findAllBySenderAndReceiver(Long senderId, Long receiverId,
		Pageable pageable) {
		QChat chat = QChat.chat;

		List<ChatMessageSimpleResponse> content = jpaQueryFactory
			.select(Projections.constructor(
				ChatMessageSimpleResponse.class,
				chat.sender,
				chat.content,
				chat.sentAt
			))
			.from(chat)
			.where(
				(chat.sender.id.eq(senderId).and(chat.receiver.id.eq(receiverId)))
					.or(chat.sender.id.eq(receiverId).and(chat.receiver.id.eq(senderId))))
			.orderBy(chat.sentAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize() + 1) // Slice는 기본 size의 +1 까지 추출하여 확인해서 다음 페이지가 있는지만 파악함
			.fetch();

		boolean hasNext = content.size() > pageable.getPageSize();

		// 다음 페이지가 존재하는지 확인 후 삭제, 인덱스 오류 방지
		if(hasNext){
			content.remove(pageable.getPageSize());
		}

		return new SliceImpl<>(content,pageable,hasNext);
	}

	// 이후 커서 기반 페이징으로 리팩토링 할 예정(고도화 작업 때)
}
