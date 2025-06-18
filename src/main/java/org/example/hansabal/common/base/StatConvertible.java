package org.example.hansabal.common.base;

import java.time.LocalDate;

public interface StatConvertible {
	LocalDate getDate();

	Integer getOrderCount();

	Long getTotalSales();
}
