package com.orange.score.model.match;

import java.util.List;

public class MatchGroup {
	public String leagueName;
	public List<Match> children;
	
	public MatchGroup(String leagueName, List<Match> children) {
		super();
		this.leagueName = leagueName;
		this.children = children;
	}
	
}
