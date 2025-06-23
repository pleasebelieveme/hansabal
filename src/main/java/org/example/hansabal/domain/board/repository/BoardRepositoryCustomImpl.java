package org.example.hansabal.domain.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.entity.QBoard;
import org.example.hansabal.domain.users.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BoardSimpleResponse> searchByCategoryAndKeyword(BoardCategory category, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;

       BooleanBuilder flag = new BooleanBuilder();

       if (category != null && category != BoardCategory.ALL) {
           flag.and(board.category.eq(category));
       }
       if (keyword != null && !keyword.isBlank()) {
           flag.and(board.title.contains(keyword).or(board.content.contains(keyword)));
       }

        List<BoardSimpleResponse> contents = queryFactory
                .select(Projections.constructor(BoardSimpleResponse.class,
                        board.user.nickname,
                        board.category,
                        board.title,
                        board.viewCount,
                        board.dibCount))
                .from(board)
                .join(board.user,user)
                .where(flag)
                .orderBy(board.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(board.count())
                .from(board)
                .where(flag)
                .fetchOne();

        return new PageImpl<>(contents, pageable, total);
    }
}
