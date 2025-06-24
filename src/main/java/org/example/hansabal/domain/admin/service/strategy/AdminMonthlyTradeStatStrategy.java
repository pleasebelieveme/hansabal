package org.example.hansabal.domain.admin.service.strategy;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.repository.AdminProductTradeStatMonthlyRepository;
import org.example.hansabal.domain.admin.request.ProductTradeStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductTradeStatRequest;
import org.example.hansabal.domain.admin.response.ProductTradeStatResponse;
import org.example.hansabal.domain.batch.entity.AdminProductTradeStatMonthly;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AdminMonthlyTradeStatStrategy implements AdminProductTradeStatStrategy {

	private final AdminProductTradeStatMonthlyRepository monthlyRepo;

	@Override
	public ProductTradeStatPeriodType getPeriodType() {
		return ProductTradeStatPeriodType.MONTHLY;
	}

	@Override
	public ProductTradeStatResponse getStatistics(ProductTradeStatRequest request) {
		List<AdminProductTradeStatMonthly> stats = monthlyRepo.findAllByDateRange(request.from(), request.to());
		return toResponse(stats);
	}
}
