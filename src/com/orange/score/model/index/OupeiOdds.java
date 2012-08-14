package com.orange.score.model.index;

import android.util.Log;

public class OupeiOdds extends Odds{
	String homeWinChupei;
	String drawWinChupei;
	String awayWinChupei;
	String homeWinInstantOdds;
	String drawInstantOdds;
	String awayWinInstantOdds;
	
	public OupeiOdds(String matchId, String companyId, String oddsId,
			String homeWinChupei, String drawWinChupei,
			String awayWinChupei, String homeWinInstantOdds,
			String drawInstantOdds, String awayWinInstantOdds) {
		super(matchId, companyId, oddsId);
		this.homeWinChupei = homeWinChupei;
		this.drawWinChupei = drawWinChupei;
		this.awayWinChupei = awayWinChupei;
		this.homeWinInstantOdds = homeWinInstantOdds;
		this.drawInstantOdds = drawInstantOdds;
		this.awayWinInstantOdds = awayWinInstantOdds;
	}
	
	public void updateOdds(String newHomeTeamOdds, String newAwayTeamOdds, String newInstantOdds) {
		if (Double.parseDouble(newHomeTeamOdds) > Double.parseDouble(homeWinInstantOdds)) {
			this.homeTeamOddsFlag = 1;
		} else if (Double.parseDouble(newHomeTeamOdds) < Double.parseDouble(homeWinInstantOdds)) {
			this.homeTeamOddsFlag = -1;
		} else {
			this.homeTeamOddsFlag = 0;
		}
		if (Double.parseDouble(newAwayTeamOdds) > Double.parseDouble(awayWinInstantOdds)) {
			this.awayTeamOddsFlag = 1;
		} else if (Double.parseDouble(newAwayTeamOdds) < Double.parseDouble(awayWinInstantOdds)) {
			this.awayTeamOddsFlag = -1;
		} else {
			this.awayTeamOddsFlag = 0;
		}
		if (Double.parseDouble(newInstantOdds) > Double.parseDouble(drawInstantOdds)) {
			this.pankouFlag = 1;
		} else if (Double.parseDouble(newInstantOdds) < Double.parseDouble(drawInstantOdds)) {
			this.pankouFlag = -1;
		} else {
			this.pankouFlag = 0;
		}
		this.homeWinInstantOdds = newHomeTeamOdds;
		this.awayWinInstantOdds = newAwayTeamOdds;
		this.drawInstantOdds = newInstantOdds;
		if (homeTeamOddsFlag != 0 || awayTeamOddsFlag !=0 || pankouFlag != 0) {
			Log.d("OupeiOdds Update", "matchId = " + matchId +
										",companyId= " + companyId + 
										",homeWinInstantOdds = " + homeWinInstantOdds + 
										",drawInstantOdds = " + drawInstantOdds +
										",awayWinInstantOdds = " + awayWinInstantOdds);
			lastModifyTime = System.currentTimeMillis();
		}
	}


	@Override
	public String toString() {
		return "OupeiOdds [matchId=" + matchId + ", companyId=" + companyId
				+ ", oddsId=" + oddsId + ", homeWinChupei=" + homeWinChupei
				+ ", drawWinChupei=" + drawWinChupei + ", awayWinChupei="
				+ awayWinChupei + ", homeWinInstantOdds=" + homeWinInstantOdds
				+ ", drawInstantOdds=" + drawInstantOdds
				+ ", awayWinInstantOdds=" + awayWinInstantOdds + "]";
	}

	public String getHomeWinChupei() {
		return homeWinChupei;
	}

	public void setHomeWinChupei(String homeWinChupei) {
		this.homeWinChupei = homeWinChupei;
	}

	public String getDrawWinChupei() {
		return drawWinChupei;
	}

	public void setDrawWinChupei(String drawWinChupei) {
		this.drawWinChupei = drawWinChupei;
	}

	public String getAwayWinChupei() {
		return awayWinChupei;
	}

	public void setAwayWinChupei(String awayWinChupei) {
		this.awayWinChupei = awayWinChupei;
	}

	public String getHomeWinInstantOdds() {
		return homeWinInstantOdds;
	}

	public void setHomeWinInstantOdds(String homeWinInstantOdds) {
		this.homeWinInstantOdds = homeWinInstantOdds;
	}

	public String getDrawInstantOdds() {
		return drawInstantOdds;
	}

	public void setDrawInstantOdds(String drawInstantOdds) {
		this.drawInstantOdds = drawInstantOdds;
	}

	public String getAwayWinInstantOdds() {
		return awayWinInstantOdds;
	}

	public void setAwayWinInstantOdds(String awayWinInstantOdds) {
		this.awayWinInstantOdds = awayWinInstantOdds;
	}
}
