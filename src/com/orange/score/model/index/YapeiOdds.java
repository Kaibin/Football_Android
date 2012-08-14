package com.orange.score.model.index;

import android.util.Log;

public class YapeiOdds extends Odds{
	String chupan;
	String homeTeamChupan;
	String awayTemaChupan;
	String instantOdds;
	String homeTeamOdds;
	String awayTemaOdds;
	
	public YapeiOdds(String matchId, String companyId, String oddsId,
			String chupan, String homeTeamChupan,
			String awayTemaChupan, String instantOdds, String homeTeamOdds,
			String awayTemaOdds) {
		super(matchId, companyId, oddsId);
		this.chupan = chupan;
		this.homeTeamChupan = homeTeamChupan;
		this.awayTemaChupan = awayTemaChupan;
		this.instantOdds = instantOdds;
		this.homeTeamOdds = homeTeamOdds;
		this.awayTemaOdds = awayTemaOdds;
	}
	
	public void updateOdds(String newHomeTeamOdds, String newAwayTeamOdds, String newInstantOdds) {
		if (Double.parseDouble(newHomeTeamOdds) > Double.parseDouble(homeTeamOdds)) {
			this.homeTeamOddsFlag = 1;
		} else if (Double.parseDouble(newHomeTeamOdds) < Double.parseDouble(homeTeamOdds)) {
			this.homeTeamOddsFlag = -1;
		} else {
			this.homeTeamOddsFlag = 0;
		}
		if (Double.parseDouble(newAwayTeamOdds) > Double.parseDouble(awayTemaOdds)) {
			this.awayTeamOddsFlag = 1;
		} else if (Double.parseDouble(newAwayTeamOdds) < Double.parseDouble(awayTemaOdds)) {
			this.awayTeamOddsFlag = -1;
		} else {
			this.awayTeamOddsFlag = 0;
		}
		if (Double.parseDouble(newInstantOdds) > Double.parseDouble(instantOdds)) {
			this.pankouFlag = 1;
		} else if (Double.parseDouble(newInstantOdds) < Double.parseDouble(instantOdds)) {
			this.pankouFlag = -1;
		} else {
			this.pankouFlag = 0;
		}
		this.homeTeamOdds = newHomeTeamOdds;
		this.awayTemaOdds = newAwayTeamOdds;
		this.instantOdds = newInstantOdds;
		if (homeTeamOddsFlag != 0 || awayTeamOddsFlag !=0 || pankouFlag != 0) {
			Log.d("YapeiOdds Update", "matchId = " + matchId +
										",companyId= " + companyId + 
										",homeTeamOdds = " + homeTeamOdds + 
										",instantOdds = " + instantOdds +
										",awayTeamOdds = " + awayTemaOdds);
			lastModifyTime = System.currentTimeMillis();
		}
	}

	@Override
	public String toString() {
		return "YapeiOdds [matchId=" + matchId + ", companyId=" + companyId
				+ ", oddsId=" + oddsId + ", chupan=" + chupan
				+ ", homeTeamChupan=" + homeTeamChupan + ", awayTemaChupan="
				+ awayTemaChupan + ", instantOdds=" + instantOdds
				+ ", homeTeamOdds=" + homeTeamOdds + ", awayTemaOdds="
				+ awayTemaOdds + "]";
	}

	public String getChupan() {
		return chupan;
	}

	public void setChupan(String chupan) {
		this.chupan = chupan;
	}

	public String getHomeTeamChupan() {
		return homeTeamChupan;
	}

	public void setHomeTeamChupan(String homeTeamChupan) {
		this.homeTeamChupan = homeTeamChupan;
	}

	public String getAwayTemaChupan() {
		return awayTemaChupan;
	}

	public void setAwayTemaChupan(String awayTemaChupan) {
		this.awayTemaChupan = awayTemaChupan;
	}

	public String getInstantOdds() {
		return instantOdds;
	}

	public void setInstantOdds(String instantOdds) {
		this.instantOdds = instantOdds;
	}

	public String getHomeTeamOdds() {
		return homeTeamOdds;
	}

	public void setHomeTeamOdds(String homeTeamOdds) {
		this.homeTeamOdds = homeTeamOdds;
	}

	public String getAwayTemaOdds() {
		return awayTemaOdds;
	}

	public void setAwayTemaOdds(String awayTemaOdds) {
		this.awayTemaOdds = awayTemaOdds;
	}
	
}
