package org.example.hansabal.domain.board.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.hansabal.domain.users.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;         // 게시글ID (PK)

    @Column(nullable = false)
    private Long userId;         // 유저식별자 (작성자)

    @Column(nullable = false)
    private Long commentWriter;  // 댓글작성자

    @Column(nullable = false, length = 30)
    private String category;     // 게시글 카테고리

    @Column(nullable = false, length = 100)
    private String title;        // 게시글 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;      // 내용

    @Column(nullable = false)
    private LocalDateTime createdAt;   // 작성시간

    @Column(nullable = false)
    private LocalDateTime updatedAt;   // 수정시간

    @Column(nullable = false)
    private Integer viewCount = 0;     // 조회수

    // (수정/생성 편의 메서드, Setter 없이 사용하려면 아래처럼 커스텀 메서드 활용)
    public void update(String category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }


}
