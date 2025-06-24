package org.example.hansabal.domain.admin.repository;

import org.example.hansabal.domain.batch.entity.AdminProductTradeStatDaily;

import java.time.LocalDate;
import java.util.List;

public interface AdminProductTradeStatDailyQueryRepository {
	List<AdminProductTradeStatDaily> findAllByDateRange(LocalDate from, LocalDate to);
}
