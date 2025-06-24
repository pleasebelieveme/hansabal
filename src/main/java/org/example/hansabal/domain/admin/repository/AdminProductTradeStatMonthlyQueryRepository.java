package org.example.hansabal.domain.admin.repository;

import org.example.hansabal.domain.batch.entity.AdminProductTradeStatMonthly;
import java.time.LocalDate;
import java.util.List;

public interface AdminProductTradeStatMonthlyQueryRepository {
	List<AdminProductTradeStatMonthly> findAllByDateRange(LocalDate from, LocalDate to);
}
