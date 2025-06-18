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
@Table(name = "admin_Product_order_stats_daily")
public class AdminProductOrderStatDaily extends AdminProductOrderStat implements StatConvertible {

    public AdminProductOrderStatDaily(LocalDate date, Integer orderCount, Long totalSales) {
        super(date, orderCount, totalSales);
    }

}
