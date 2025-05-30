package org.example.hansabal.domain.comment.entity;

import java.util.ArrayList;
import java.util.List;

import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.board.entity.Board;
import org.example.hansabal.domain.users.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@Getter
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String contents;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;

	// 좋아요 필드 및 메서드
	@Column(nullable = false,columnDefinition = "int default 0")
	private int dibCount = 0;

	public void increaseDibs() {
		this.dibCount++;
	}

	public void decreaseDibs() {
		if (dibCount > 0) this.dibCount--;
	}

	public Comment(String contents, User user, Board board) {
		this.contents = contents;
		this.user = user;
		this.board = board;
	}

	public void updateContents(String contents){
		this.contents = contents;
	}
}
