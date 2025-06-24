package org.example.hansabal.domain.admin.repository;


import org.example.hansabal.domain.batch.entity.AdminProductTradeStatDaily;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AdminProductTradeStatDailyRepository
	extends JpaRepository<AdminProductTradeStatDaily, LocalDate>, AdminProductTradeStatDailyQueryRepository {

}
