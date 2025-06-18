package org.example.hansabal.domain.admin.service.strategy;

import org.example.hansabal.common.base.StatConvertible;
import org.example.hansabal.domain.admin.request.ProductOrderStatPeriodType;
import org.example.hansabal.domain.admin.request.ProductOrderStatRequest;
import org.example.hansabal.domain.admin.response.ProductOrderStatItem;
import org.example.hansabal.domain.admin.response.ProductOrderStatResponse;

import java.util.List;

public interface AdminProductOrderStatStrategy {
	ProductOrderStatPeriodType getPeriodType();

	ProductOrderStatResponse getStatistics(ProductOrderStatRequest request);

	default ProductOrderStatResponse toResponse(List<? extends StatConvertible> stats) {
		List<ProductOrderStatItem> items = stats.stream()
			.map(stat -> new ProductOrderStatItem(
				stat.getDate(),
				stat.getOrderCount(),
				stat.getTotalSales()
			))
			.toList();

		return new ProductOrderStatResponse(items);
	}
}
