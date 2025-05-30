package org.example.hansabal.domain.users.entity;

import java.util.List;

import org.example.hansabal.common.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

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

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = true)
	private UserRole userRole;

	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Board> boards;
	//
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Product> products;
	//
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Review> reviews;
	//
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Comment> comments;
	//
	// @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	// private List<Trade> trades;

	public User(String email, String password, String name, String nickname) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
	}

	public void updateUser(String nickname, String password) {
		if (nickname != null) {
			this.nickname = nickname;
		}
		if (password != null) {
			this.password = password;
		}
	}
}
