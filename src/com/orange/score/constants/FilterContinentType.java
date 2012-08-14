package com.orange.score.constants;

public enum FilterContinentType {
	INTERNATION(0), 
	EUROPE(1), 
	AMERICA(2), 
	ASIA(3), 
	OCEANIA(4), 
	AFRICA(5);

	final int value;

	FilterContinentType(int value) {
		this.value = value;
	}

	public int intValue() {
		return value;
	}
}
