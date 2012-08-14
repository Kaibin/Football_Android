package com.orange.score.model.index;

import java.util.List;

import com.orange.score.model.match.Match;

public class OddsGroup {
//	public OddsGroupTitle oddsGroupTitle;
	public Match match;
	public List<Odds> children;
	
	public OddsGroup(Match match, List<Odds> children) {
		super();
		this.match = match;
		this.children = children;
	}
}
