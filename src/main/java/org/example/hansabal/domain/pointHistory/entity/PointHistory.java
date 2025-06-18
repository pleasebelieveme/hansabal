package org.example.hansabal.domain.pointHistory.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.BaseEntity;
import org.example.hansabal.domain.users.entity.User;

@Entity
@NoArgsConstructor
@Getter
public class PointHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private int amount;

	@Column(nullable = false)
	private int remain;

	@Enumerated(EnumType.STRING)
	private PointUsed pointUsed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	public PointHistory(Integer amount, Integer remain, User user) {
		this.amount = amount;
		this.remain = remain;
		this.user = user;
		this.pointUsed = PointUsed.UNUSED;
	}

	public PointHistory(Integer amount, Integer remain, User user, PointUsed pointUsed) {
		this.amount = amount;
		this.remain = remain;
		this.user = user;
		this.pointUsed = pointUsed;
	}

	public void updateRemain(int curPoint) {
		if (curPoint == 0) {
			this.pointUsed = PointUsed.USED;
		}
		this.remain = curPoint;
	}
}
