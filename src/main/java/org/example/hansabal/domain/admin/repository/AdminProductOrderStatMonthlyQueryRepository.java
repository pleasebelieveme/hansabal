package org.example.hansabal.domain.admin.repository;

import org.example.hansabal.domain.batch.entity.AdminProductOrderStatMonthly;
import java.time.LocalDate;
import java.util.List;

public interface AdminProductOrderStatMonthlyQueryRepository {
	List<AdminProductOrderStatMonthly> findAllByDateRange(LocalDate from, LocalDate to);
}
