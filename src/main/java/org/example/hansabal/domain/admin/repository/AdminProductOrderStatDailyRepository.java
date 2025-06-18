package org.example.hansabal.domain.admin.repository;


import org.example.hansabal.domain.batch.entity.AdminProductOrderStatDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AdminProductOrderStatDailyRepository
	extends JpaRepository<AdminProductOrderStatDaily, LocalDate>, AdminProductOrderStatDailyQueryRepository {

}
