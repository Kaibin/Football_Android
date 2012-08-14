package com.orange.score.model.league;

import java.util.List;

public class League {

	public String leagueId;
	public String name;
	public String shortName;
	public boolean isTop = true;
	public int matchCount = 0;
	
	public String countryId;
	public int    type;
	public List<String>  seasonList;
	
	public League() {
		super();
	}
	
	public League(String id, String name, String isTop){
		this.leagueId = id;
		this.name = name;
		if (isTop != null && isTop.length() > 0){
			this.isTop = (Integer.parseInt(isTop) == 1) ? true : false;
		}
	}

	@Override
	public String toString() {
		return "League [isTop=" + isTop + ", leagueId=" + leagueId + ", name="
				+ name + "]";
	}

	public void increaseMatchCount() {
		matchCount ++;
	}
	
	public int getMatchCount() {
		return matchCount;
	}
	
	public String getLeagueName() {
		return this.name;
	}
	
	public boolean isTopLeague() {
		return this.isTop;
	}

	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<String> getSeasonList() {
		return seasonList;
	}

	public void setSeasonList(List<String> seasonList) {
		this.seasonList = seasonList;
	}
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
}
