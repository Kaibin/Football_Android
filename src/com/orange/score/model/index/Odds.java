package com.orange.score.model.index;

public class Odds {
	String matchId;
	String companyId;
	String oddsId;
	public int homeTeamOddsFlag = 0;
	public int awayTeamOddsFlag = 0;
	public int pankouFlag = 0;
	public long lastModifyTime = System.currentTimeMillis();
	
	static int MIN_SCORE_UPDATE_INTERVAL = 10*1000;
	public boolean isOddsJustUpdate() {
		return (System.currentTimeMillis() - lastModifyTime <= MIN_SCORE_UPDATE_INTERVAL) ? true : false;
	}	
	
	public Odds(String matchId, String companyId, String oddsId) {
		super();
		this.matchId = matchId;
		this.companyId = companyId;
		this.oddsId = oddsId;
	}
	
	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getOddsId() {
		return oddsId;
	}

	public void setOddsId(String oddsId) {
		this.oddsId = oddsId;
	}

	@Override
	public String toString() {
		return "Odds [matchId=" + matchId + ", companyId=" + companyId
				+ ", oddsId=" + oddsId + "]";
	}
}
