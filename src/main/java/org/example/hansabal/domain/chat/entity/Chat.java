package org.example.hansabal.domain.chat.entity;

import java.time.LocalDateTime;

import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Chat extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id",nullable = false)
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id",nullable = false)
	private User receiver;

	private String content;

	private LocalDateTime sentAt;

	@PrePersist
	public void prePersist() {
		this.sentAt = LocalDateTime.now();
	}

	public Chat(User sender, User receiver, String content) {
		this.sender = sender;
		this.receiver = receiver;
		this.content = content;
	}
}
