package org.example.hansabal.domain.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.example.hansabal.domain.chat.dto.response.ChatCursorResponse;
import org.example.hansabal.domain.chat.dto.response.ChatCursorSliceResponse;
import org.example.hansabal.domain.chat.entity.QChat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRepositoryImpl implements ChatRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public ChatCursorSliceResponse findAllBySenderAndReceiver(Long senderId, Long receiverId,
		LocalDateTime cursor, Pageable pageable) {
		QChat chat = QChat.chat;

		// 양방향 채팅 조건 : 보낸 메시지, 받은 메시지
		BooleanExpression baseCondition =
			(chat.sender.id.eq(senderId).and(chat.receiver.id.eq(receiverId)))
				.or(chat.sender.id.eq(receiverId).and(chat.receiver.id.eq(senderId)));
		/*
		 * 커서 기반 페이징 조건:
		 * 이전 페이지의 마지막 메시지의 sentAt 시간보다 더 과거의 메시지만 조회
		 * 즉, DESC 정렬이므로 cursor보다 작은 sentAt이 다음 페이지에 해당
		 * */
		BooleanExpression cursorCondition = cursor != null ? chat.sentAt.lt(cursor) : null;

		List<ChatCursorResponse> content = jpaQueryFactory
			.select(Projections.constructor(
				ChatCursorResponse.class,
				chat.sender,
				chat.content,
				chat.sentAt
			))
			.from(chat)
			.where(baseCondition.and(cursorCondition))
			.orderBy(chat.sentAt.desc(),chat.id.desc())
			// .offset(pageable.getOffset()) 커서 기반 페이징은 offset이 필요없음
			.limit(pageable.getPageSize() + 1) // Slice는 기본 size의 +1 까지 추출하여 확인해서 다음 페이지가 있는지만 파악함
			.fetch();

		boolean hasNext = content.size() > pageable.getPageSize();

		// 다음 페이지가 존재하는지 확인 후 삭제, 인덱스 오류 방지
		if(hasNext){
			content.remove(pageable.getPageSize());
		}

		LocalDateTime nextCursor = hasNext ? content.get(content.size()-1).sentAt() : null;

		return new ChatCursorSliceResponse(content,nextCursor,hasNext);
	}
}
