package org.example.hansabal.domain.trade.entity;

public enum TradeStatus {

	FINISHED("거래 완료됨");

	private final String description;

	TradeStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
