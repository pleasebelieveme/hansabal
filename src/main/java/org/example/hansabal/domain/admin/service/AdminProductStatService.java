package org.example.hansabal.domain.admin.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.admin.exception.StatErrorCode;
import org.example.hansabal.domain.admin.request.ProductOrderStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductOrderStatRequest;
import org.example.hansabal.domain.admin.response.ProductOrderStatResponse;
import org.example.hansabal.domain.admin.service.strategy.AdminProductOrderStatStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminProductStatService {

	private final List<AdminProductOrderStatStrategy> ProductOrderStatStrategies;

	private Map<ProductOrderStatPeriodType, AdminProductOrderStatStrategy> ProductOrderStatMap;

	@PostConstruct
	private void initStrategyMap() {
		this.ProductOrderStatMap = ProductOrderStatStrategies.stream()
			.collect(Collectors.toMap(
				AdminProductOrderStatStrategy::getPeriodType,
				Function.identity()
			));
	}

	public ProductOrderStatResponse getOrderStat(ProductOrderStatRequest req) {
		AdminProductOrderStatStrategy strategy = ProductOrderStatMap.get(req.period());
		if (strategy == null) {
			throw new BizException(StatErrorCode.UNSUPPORTED_STAT_PERIOD);
		}
		return strategy.getStatistics(req);
	}
}
