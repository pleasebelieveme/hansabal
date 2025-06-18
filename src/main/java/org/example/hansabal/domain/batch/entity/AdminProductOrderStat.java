package org.example.hansabal.domain.batch.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@MappedSuperclass
public class AdminProductOrderStat {

	@Id
	@Column(nullable = false)
	private LocalDate date;

	@Column(nullable = false)
	private Integer orderCount;

	@Column(nullable = false)
	private Long totalSales;
}
