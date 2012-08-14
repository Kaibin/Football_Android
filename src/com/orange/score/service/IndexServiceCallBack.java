package com.orange.score.service;

import com.orange.score.network.ResultCodeType;

public interface IndexServiceCallBack {

	void loadAllCompanyFinish(ResultCodeType success);
	void loadOddsDataFinish(ResultCodeType success);

}
