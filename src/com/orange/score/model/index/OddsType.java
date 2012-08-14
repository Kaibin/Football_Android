package com.orange.score.model.index;

public enum OddsType {
	YAPEI(1), DAXIAO(2), OUPEI(3);
	
	final int value;
	OddsType(int value){
		this.value = value;
	}

	public int intValue() {
		return value;
	}
}
