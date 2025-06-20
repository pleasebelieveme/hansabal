package org.example.hansabal.domain.board.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.board.entity.BoardCategory;
import org.example.hansabal.domain.comment.dto.response.CommentPageResponse;
import org.example.hansabal.domain.users.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponse {
    private Long id;
    private Long userId;
    private String nickname;
    private String email;
    private BoardCategory category;
    private String title;
    private String content;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CommentPageResponse> comments;
    private int likeCount;
    private boolean likedByMe;
    private int dibCount;

    public static BoardResponse from(Board board) {
        User user = board.getUser();
        return BoardResponse.builder()
                .id(board.getId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .category(board.getCategory()) // or .toString()
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .dibCount(board.getDibCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .comments(null)
                .likeCount(0)
                .likedByMe(false)
                .build();
    }

}
