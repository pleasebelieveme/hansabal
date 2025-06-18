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
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
@Entity
@Table(name = "Product_order_stats_daily")
public class ProductOrderStatDaily implements StatConvertible {

	@EmbeddedId
	private ProductOrderStatId id;

	@Column(nullable = false)
	private Integer orderCount;

	@Column(nullable = false)
	private Long totalSales;

	@Override
	public LocalDate getDate() {
		return this.id.getDate();
	}

	public void update(Integer orderCount, Long totalSales) {
		this.orderCount = orderCount;
		this.totalSales = totalSales;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ProductOrderStatDaily entity) {
			return Objects.equals(id, entity.id)
				&& Objects.equals(orderCount, entity.orderCount)
				&& Objects.equals(totalSales, entity.totalSales);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, orderCount, totalSales);
	}
}
