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
@Table(name = "admin_Product_Trade_stats_monthly")
public class AdminProductTradeStatMonthly extends AdminProductTradeStat implements StatConvertible {

	public AdminProductTradeStatMonthly(LocalDate date, Integer TradeCount, Long totalSales) {
		super(date, TradeCount, totalSales);
	}
}
