package org.example.hansabal.domain.chat.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChatCursorSliceResponse(
	List<ChatCursorResponse> content,
	LocalDateTime nextCursor,
	boolean hasNext
) {
	/*
	 * ChatCursorResponse는 단일 메시지 표현용이고
	 * ChatCursorSliceResponse는 페이징된 전체 결과와 다음 요청을 위한 정보를 넘겨주는 래퍼(Wrapper)클래스이다
	 *
	 **/
}
