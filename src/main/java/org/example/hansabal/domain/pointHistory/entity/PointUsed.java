package org.example.hansabal.domain.pointHistory.entity;

public enum PointUsed {
	USED("Used"), UNUSED("Unused"), DEDUCTED("Deducted");

	private final String value;

	PointUsed(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
