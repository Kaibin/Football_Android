package com.orange.score.model.match;

public enum FilterMatchStatusType {

	ALL(0),
	NOT_STARTED(1),
	ONGOING(2),
	FINISH(3),
	FOLLOWED(4);
	
	final int value;
	FilterMatchStatusType(int value){
		this.value = value;
	}
	
	public int intValue() {
		return value;
	}	
}
