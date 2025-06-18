package org.example.hansabal.domain.admin.service.strategy;


import lombok.RequiredArgsConstructor;
import org.example.hansabal.domain.admin.repository.AdminProductOrderStatMonthlyRepository;
import org.example.hansabal.domain.admin.request.ProductOrderStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductOrderStatRequest;
import org.example.hansabal.domain.admin.response.ProductOrderStatResponse;
import org.example.hansabal.domain.batch.entity.AdminProductOrderStatMonthly;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AdminMonthlyOrderStatStrategy implements AdminProductOrderStatStrategy {

	private final AdminProductOrderStatMonthlyRepository monthlyRepo;

	@Override
	public ProductOrderStatPeriodType getPeriodType() {
		return ProductOrderStatPeriodType.MONTHLY;
	}

	@Override
	public ProductOrderStatResponse getStatistics(ProductOrderStatRequest request) {
		List<AdminProductOrderStatMonthly> stats = monthlyRepo.findAllByDateRange(request.from(), request.to());
		return toResponse(stats);
	}
}
