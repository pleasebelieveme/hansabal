package org.example.hansabal.domain.board.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "boards")
public class Board  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Builder
    public Board(User user, String category, String title, String content, Integer viewCount) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount != null ? viewCount : 0;
    }

    public void update(String category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    // 편의 메서드
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}



