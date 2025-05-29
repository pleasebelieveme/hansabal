package org.example.hansabal.domain.board.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.users.entity.User;

import java.time.LocalDateTime;

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

    // ⭐ 정적 팩토리 메서드
    public static BoardResponse from(Board board) {
        User user = board.getUser();
        return BoardResponse.builder()
                .postId(board.getPostId())
                .userId(user != null ? user.getId() : null)
                .nickname(user != null ? user.getNickname() : null)
                .email(user != null ? user.getEmail() : null)
                .category(board.getCategory())
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
