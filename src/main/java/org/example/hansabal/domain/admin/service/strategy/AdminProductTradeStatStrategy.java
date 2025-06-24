package org.example.hansabal.domain.admin.service.strategy;

import org.example.hansabal.common.base.StatConvertible;
import org.example.hansabal.domain.admin.request.ProductTradeStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductTradeStatRequest;
import org.example.hansabal.domain.admin.response.ProductTradeStatItem;
import org.example.hansabal.domain.admin.response.ProductTradeStatResponse;

import java.util.List;

public interface AdminProductTradeStatStrategy {
	ProductTradeStatPeriodType getPeriodType();

	ProductTradeStatResponse getStatistics(ProductTradeStatRequest request);

	default ProductTradeStatResponse toResponse(List<? extends StatConvertible> stats) {
		List<ProductTradeStatItem> items = stats.stream()
			.map(stat -> new ProductTradeStatItem(
				stat.getDate(),
				stat.getTradeCount(),
				stat.getTotalSales()
			))
			.toList();

		return new ProductTradeStatResponse(items);
	}
}
