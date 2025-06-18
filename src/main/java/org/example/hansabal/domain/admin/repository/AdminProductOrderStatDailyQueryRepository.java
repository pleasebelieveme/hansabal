package org.example.hansabal.domain.admin.repository;

import org.example.hansabal.domain.batch.entity.AdminProductOrderStatDaily;

import java.time.LocalDate;
import java.util.List;

public interface AdminProductOrderStatDailyQueryRepository {
	List<AdminProductOrderStatDaily> findAllByDateRange(LocalDate from, LocalDate to);
}
