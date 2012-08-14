package com.orange.score.service;

import java.util.Set;

import com.orange.score.model.index.Odds;

public interface OddsUpdateChangeCallBack {

	public void notifyOddsUpdate(Set<Odds> list);
}
