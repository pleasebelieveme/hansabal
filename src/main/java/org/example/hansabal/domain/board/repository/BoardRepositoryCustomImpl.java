package org.example.hansabal.domain.board.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.board.dto.response.BoardSimpleResponse;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.board.entity.QBoard;
import org.example.hansabal.domain.trade.exception.TradeErrorCode;
import org.example.hansabal.domain.users.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BoardRepositoryCustomImpl implements BoardRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private BooleanExpression nameContaining(String query, StringPath field) {//nameContaining 정의
        QBoard board = QBoard.board;
        if (query == null) {
            log.error(TradeErrorCode.NO_SEARCH_QUERY.getMessage());
            throw new BizException(TradeErrorCode.NO_SEARCH_QUERY);
        }

        return Expressions.booleanTemplate("fulltext_match({0}, {1})", field, query);
    }

    @Override
    public Page<BoardSimpleResponse> searchByCategoryAndKeyword(BoardCategory category, String keyword, Pageable pageable) {
        QBoard board = QBoard.board;
        QUser user = QUser.user;

       BooleanBuilder flag = new BooleanBuilder();

       if (category != null && category != BoardCategory.ALL) {
           flag.and(board.category.eq(category));
       }
       if (keyword != null && !keyword.isBlank()) {
           flag.and(nameContaining(keyword, board.title).or(nameContaining(keyword, board.title)));
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

        Long total = Optional.ofNullable(
                queryFactory
                        .select(board.count())
                        .from(board)
                        .where(flag)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(contents, pageable, total);
    }
}
