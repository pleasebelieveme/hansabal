package org.example.hansabal.domain.admin.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.hansabal.common.exception.BizException;
import org.example.hansabal.domain.admin.exception.StatErrorCode;
import org.example.hansabal.domain.admin.request.ProductTradeStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductTradeStatRequest;
import org.example.hansabal.domain.admin.response.ProductTradeStatResponse;
import org.example.hansabal.domain.admin.service.strategy.AdminProductTradeStatStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminProductStatService {

	private final List<AdminProductTradeStatStrategy> ProductTradeStatStrategies;

	private Map<ProductTradeStatPeriodType, AdminProductTradeStatStrategy> ProductTradeStatMap;

	@PostConstruct
	private void initStrategyMap() {
		this.ProductTradeStatMap = ProductTradeStatStrategies.stream()
			.collect(Collectors.toMap(
				AdminProductTradeStatStrategy::getPeriodType,
				Function.identity()
			));
	}

	public ProductTradeStatResponse getTradeStat(ProductTradeStatRequest req) {
		AdminProductTradeStatStrategy strategy = ProductTradeStatMap.get(req.period());
		if (strategy == null) {
			throw new BizException(StatErrorCode.UNSUPPORTED_STAT_PERIOD);
		}
		return strategy.getStatistics(req);
	}
}
