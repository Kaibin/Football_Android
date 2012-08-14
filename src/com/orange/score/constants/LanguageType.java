package com.orange.score.constants;

import android.R.integer;

public enum LanguageType {
	MANDARY (0),
	CANTONESE (1),
	CROWN (2);
	
	int value;
	
	LanguageType(int value){
		this.value = value;
	}

	public int intValue() {
		return value;
	}
	
	public void setIntValue(int value) {
		this.value = value;
	}
	
	
	
}
