package com.orange.score.service;

import com.orange.score.network.ResultCodeType;

public interface RepositoryServiceCallBack {

	void loadAllRepositoryFinish(ResultCodeType success);
	
	void loadGroupInfoFinish(ResultCodeType success);

	void loadRoundInfoFinish(ResultCodeType success);

}
