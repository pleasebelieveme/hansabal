package org.example.hansabal.domain.board.service;


import org.example.hansabal.domain.board.dto.response.BoardResponse;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.users.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardMapper {
    public BoardResponse toResponse(Board board, List<CommentPageResponse> comments, int likeCount, boolean likedByMe) {
        User user = board.getUser();

        return BoardResponse.builder()
                .id(board.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .comments(comments)
                .likeCount(likeCount)
                .likedByMe(likedByMe)
                .build();
    }

    /**
     * 기본값 사용 (댓글 없음, 좋아요 없음)
     */
    public BoardResponse toResponse(Board board) {
        return toResponse(board, null, 0, false);
    }
}
