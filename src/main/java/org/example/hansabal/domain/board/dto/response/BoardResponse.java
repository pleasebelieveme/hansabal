package org.example.hansabal.domain.board.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.comment.dto.response.CommentResponse;
import org.example.hansabal.domain.users.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponse {
    private Long postId;
    private Long userId;
    private String nickname;
    private String email;
    private String category;
    private String title;
    private String content;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<CommentResponse> comments;
    private int likeCount;
    private boolean likedByMe;

    public static BoardResponse from(Board board) {
        User user = board.getUser();
        return BoardResponse.builder()
                .postId(board.getPostId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .category(board.getCategory().getDisplayName()) // or .toString()
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .comments(null)
                .likeCount(0)
                .likedByMe(false)
                .build();
    }

    // ⭐ 정적 팩토리 메서드
    public static BoardResponse from(Board board, List<CommentResponse> comments, int likeCount, boolean likedByMe) {
        User user = board.getUser();
        return BoardResponse.builder()
                .postId(board.getPostId())
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .category(board.getCategory().getDisplayName())
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

}
