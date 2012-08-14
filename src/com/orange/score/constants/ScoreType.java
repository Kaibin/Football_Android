package com.orange.score.constants;

public enum ScoreType {
	
	ALL(0),
	FIRST(1),
	ZUCAI(2),
	JINGCAI(3),
	DANCHANGE(4);

	final int value;
	ScoreType(int value){
		this.value = value;
	}
	
	public int intValue() {
		return value;
	}

}
