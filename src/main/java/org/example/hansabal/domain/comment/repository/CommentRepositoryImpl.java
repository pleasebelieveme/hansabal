package org.example.hansabal.domain.comment.repository;

import java.util.List;

import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.comment.entity.QComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
	/* 쿼리 DSL의 Projections를 활용한 쿼리 최적화
	* */

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<CommentPageResponse> findByBoardId(Long boardId, Pageable pageable) {
		QComment comment = QComment.comment;

		List<CommentPageResponse> content = queryFactory
			.select(Projections.constructor(
				CommentPageResponse.class,
				comment.contents,
				comment.dibCount
			))
			.from(comment)
			.where(comment.board.id.eq(boardId).and(comment.deletedAt.isNull()))
			.orderBy(comment.createdAt.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(comment.id)
			.from(comment)
			.where(comment.board.id.eq(boardId))
			.fetch()
			.size();

		return new PageImpl<>(content,pageable,total);
	}
}
