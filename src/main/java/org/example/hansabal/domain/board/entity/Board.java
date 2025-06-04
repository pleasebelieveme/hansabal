package org.example.hansabal.domain.board.entity;


import jakarta.persistence.*;
import lombok.*;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;



@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "boards")
public class Board  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BoardCategory category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer viewCount = 0;

    // 좋아요 필드 및 메서드
    @Column(nullable = false,columnDefinition = "int default 0")
    private int dibCount = 0;

    public void increaseDibs() {
        this.dibCount++;
    }

    public void decreaseDibs() {
        if (dibCount > 0) this.dibCount--;
    }

    @Builder
    public Board(User user, BoardCategory category, String title, String content, Integer viewCount) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount != null ? viewCount : 0;
    }

    public void update(BoardCategory category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    // 편의 메서드
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}



