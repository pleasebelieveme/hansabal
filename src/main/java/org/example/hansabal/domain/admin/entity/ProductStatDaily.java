package org.example.hansabal.domain.admin.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hansabal.common.base.StatConvertible;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "admin_product_stats_daily")
public class ProductStatDaily extends AdminProductOrderStat implements StatConvertible {

	public ProductStatDaily(LocalDate date, Integer orderCount, Long totalSales) {
		super(date, orderCount, totalSales);
	}

}
