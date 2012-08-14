package com.orange.score.service;

import com.orange.score.network.ResultCodeType;

public interface MatchServiceCallBack {

	public void loadAllMatchFinish(ResultCodeType resultCode);
	
	public void loadMatchDetailFinish(ResultCodeType resultCode);

}
