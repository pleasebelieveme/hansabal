package org.example.hansabal.domain.users.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.comment.entity.Dib;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.domain.product.entity.Product;
import org.example.hansabal.domain.wallet.entity.Wallet;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.annotation.LastModifiedDate;

@EntityScan(basePackages = "org.example.hansabal.domain.users.entity")
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String nickname;

	private LocalDateTime lastLoginAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private UserRole userRole;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserStatus userStatus = UserStatus.ACTIVE;

	public User(Long id, String email, String password, String name, String nickname, UserRole userRole) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.userRole = userRole;
	}

	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Board> boards;
	//

	//
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Review> reviews;
	//
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Comment> comments;
	//
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Trade> trades;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Dib> dibs;

	@OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
	private Wallet wallet;

	public void updateUser(String nickname, String password) {
		if (nickname != null) {
			this.nickname = nickname;
		}
		if (password != null) {
			this.password = password;
		}
	}

	public void updateLastLoginTime() {
		this.lastLoginAt = LocalDateTime.now();
	}

	public void markAsDormant() {
		this.userStatus = UserStatus.DORMANT;
	}
}
