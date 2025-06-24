package org.example.hansabal.domain.admin.service.strategy;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.repository.AdminProductTradeStatDailyRepository;
import org.example.hansabal.domain.admin.request.ProductTradeStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductTradeStatRequest;
import org.example.hansabal.domain.admin.response.ProductTradeStatResponse;
import org.example.hansabal.domain.batch.entity.AdminProductTradeStatDaily;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AdminDailyTradeStatStrategy implements AdminProductTradeStatStrategy {

	private final AdminProductTradeStatDailyRepository dailyRepo;

	@Override
	public ProductTradeStatPeriodType getPeriodType() {
		return ProductTradeStatPeriodType.DAILY;
	}

	@Override
	public ProductTradeStatResponse getStatistics(ProductTradeStatRequest request) {
		List<AdminProductTradeStatDaily> stats = dailyRepo.findAllByDateRange(request.from(), request.to());
		return toResponse(stats);
	}
}
