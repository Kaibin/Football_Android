package com.orange.score.constants;

public enum LeagueType {
	LEAGUE(1),
	CUP(2);
	int value;
	
	LeagueType(int value) {
		this.value = value;
	}
	
	public int intValue() {
		return value;
	}
}
