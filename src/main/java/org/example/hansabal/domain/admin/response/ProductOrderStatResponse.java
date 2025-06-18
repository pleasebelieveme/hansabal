package org.example.hansabal.domain.admin.response;

import java.util.List;

public record ProductOrderStatResponse(
	List<ProductOrderStatItem> stats
) {

}
