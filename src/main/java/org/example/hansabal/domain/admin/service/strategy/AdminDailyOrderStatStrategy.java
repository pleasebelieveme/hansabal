package org.example.hansabal.domain.admin.service.strategy;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.repository.AdminProductOrderStatDailyRepository;
import org.example.hansabal.domain.admin.request.ProductOrderStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductOrderStatRequest;
import org.example.hansabal.domain.admin.response.ProductOrderStatResponse;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatDaily;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AdminDailyOrderStatStrategy implements AdminProductOrderStatStrategy {

	private final AdminProductOrderStatDailyRepository dailyRepo;

	@Override
	public ProductOrderStatPeriodType getPeriodType() {
		return ProductOrderStatPeriodType.DAILY;
	}

	@Override
	public ProductOrderStatResponse getStatistics(ProductOrderStatRequest request) {
		List<AdminProductOrderStatDaily> stats = dailyRepo.findAllByDateRange(request.from(), request.to());
		return toResponse(stats);
	}
}
