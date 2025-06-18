package org.example.hansabal.domain.pointHistory.entity;


import jakarta.persistence.*;
import lombok.Getter;
import org.example.hansabal.common.base.BaseEntity;

@Entity
@Getter
@Table(name = "pointUseHistories")
public class PointUseHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer pointUsed;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "point_history_id", nullable = false)
	private PointHistory pointHistory;

	public PointUseHistory(PointHistory ph, Integer point) {
		this.pointHistory = ph;
		this.pointUsed = point;
	}

	public PointUseHistory() {

	}
}
