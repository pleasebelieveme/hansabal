package org.example.hansabal.domain.admin.response;

import java.util.List;

public record ProductTradeStatResponse(
	List<ProductTradeStatItem> stats
) {

}
