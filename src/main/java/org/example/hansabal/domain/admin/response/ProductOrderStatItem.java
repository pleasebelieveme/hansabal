package org.example.hansabal.domain.admin.response;

import java.time.LocalDate;

public record ProductOrderStatItem(
	LocalDate date,
	Integer orderCount,
	Long totalSales
) {

}
