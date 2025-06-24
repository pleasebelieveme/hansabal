package org.example.hansabal.domain.admin.response;

import java.time.LocalDate;

public record ProductTradeStatItem(
	LocalDate date,
	Integer TradeCount,
	Long totalSales
) {

}
