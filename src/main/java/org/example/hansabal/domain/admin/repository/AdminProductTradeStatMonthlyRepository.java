package org.example.hansabal.domain.admin.repository;



import org.example.hansabal.domain.batch.entity.AdminProductTradeStatMonthly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AdminProductTradeStatMonthlyRepository
	extends JpaRepository<AdminProductTradeStatMonthly, LocalDate>, AdminProductTradeStatMonthlyQueryRepository {

}
