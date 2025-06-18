package org.example.hansabal.domain.admin.repository;


import org.example.hansabal.domain.admin.entity.AdminProductOrderStatMonthly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AdminProductOrderStatMonthlyRepository
	extends JpaRepository<AdminProductOrderStatMonthly, LocalDate>, AdminProductOrderStatMonthlyQueryRepository {

}
