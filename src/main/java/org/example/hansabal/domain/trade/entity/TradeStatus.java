package org.example.hansabal.domain.trade.entity;

public enum TradeStatus {
	CHECKING("확인중"),
	COOKING("조리중"),
	DELIVERING("배달중"),
	FINISHED("배달완료"),
	REFUSED("거절됨");

	private final String description;

	TradeStatus(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
}
