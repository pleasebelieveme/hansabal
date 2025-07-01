package org.example.hansabal.domain.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.StatConvertible;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
@Entity
@Table(name = "Product_Trade_stats_monthly")
public class ProductTradeStatMonthly implements StatConvertible {

	@EmbeddedId
	private ProductTradeStatId id;

	@Column(nullable = false)
	private Integer TradeCount;

	@Column(nullable = false)
	private Long totalSales;

	@Override
	public LocalDate getDate() {
		return this.id.getDate();
	}

	public void add(Integer TradeCount, Long totalSales) {
		this.TradeCount += TradeCount;
		this.totalSales += totalSales;
	}
}
