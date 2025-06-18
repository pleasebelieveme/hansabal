package org.example.hansabal.domain.batch.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.StatConvertible;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "admin_Product_order_stats_monthly")
public class AdminProductOrderStatMonthly extends AdminProductOrderStat implements StatConvertible {

	public AdminProductOrderStatMonthly(LocalDate date, Integer orderCount, Long totalSales) {
		super(date, orderCount, totalSales);
	}
}
