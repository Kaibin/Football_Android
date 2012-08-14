package com.orange.score.model.index;

import android.util.Log;

public class DaxiaoOdds extends Odds{
	String chupan;
	String bigChupan;
	String smallChupan;
	String instantOdds;
	String bigOdds;
	String smallOdds;
	
	public DaxiaoOdds(String matchId, String companyId, String oddsId,
			String chupan, String bigChupan, String smallChupan,
			String instantOdds, String bigOdds, String smallOdds) {
		super(matchId, companyId, oddsId);
		this.chupan = chupan;
		this.bigChupan = bigChupan;
		this.smallChupan = smallChupan;
		this.instantOdds = instantOdds;
		this.bigOdds = bigOdds;
		this.smallOdds = smallOdds;
	}
	
	public void updateOdds(String newHomeTeamOdds, String newAwayTeamOdds, String newInstantOdds) {
		if (Double.parseDouble(newHomeTeamOdds) > Double.parseDouble(bigOdds)) {
			this.homeTeamOddsFlag = 1;
		} else if (Double.parseDouble(newHomeTeamOdds) < Double.parseDouble(bigOdds)) {
			this.homeTeamOddsFlag = -1;
		} else {
			this.homeTeamOddsFlag = 0;
		}
		if (Double.parseDouble(newAwayTeamOdds) > Double.parseDouble(smallOdds)) {
			this.awayTeamOddsFlag = 1;
		} else if (Double.parseDouble(newAwayTeamOdds) < Double.parseDouble(smallOdds)) {
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
		this.bigOdds = newHomeTeamOdds;
		this.smallOdds = newAwayTeamOdds;
		this.instantOdds = newInstantOdds;
		if (homeTeamOddsFlag != 0 || awayTeamOddsFlag !=0 || pankouFlag != 0) {
			Log.d("DaxiaoOdds Update", "matchId = " + matchId +
										",companyId= " + companyId + 
										",bigOdds = " + bigOdds + 
										",smallOdds = " + smallOdds +
										",instantOdds = " + instantOdds);
			lastModifyTime = System.currentTimeMillis();
		}
	}

	@Override
	public String toString() {
		return "DaxiaoOdds [matchId=" + matchId + ", companyId=" + companyId
				+ ", oddsId=" + oddsId + ", chupan=" + chupan + ", bigChupan="
				+ bigChupan + ", smallChupan=" + smallChupan + ", instantOdds="
				+ instantOdds + ", bigOdd=" + bigOdds + ", smallOdds="
				+ smallOdds + "]";
	}

	public String getChupan() {
		return chupan;
	}

	public void setChupan(String chupan) {
		this.chupan = chupan;
	}

	public String getBigChupan() {
		return bigChupan;
	}

	public void setBigChupan(String bigChupan) {
		this.bigChupan = bigChupan;
	}

	public String getSmallChupan() {
		return smallChupan;
	}

	public void setSmallChupan(String smallChupan) {
		this.smallChupan = smallChupan;
	}

	public String getInstantOdds() {
		return instantOdds;
	}

	public void setInstantOdds(String instantOdds) {
		this.instantOdds = instantOdds;
	}

	public String getBigOdds() {
		return bigOdds;
	}

	public void setBigOdds(String bigOdds) {
		this.bigOdds = bigOdds;
	}

	public String getSmallOdds() {
		return smallOdds;
	}

	public void setSmallOdds(String smallOdds) {
		this.smallOdds = smallOdds;
	}
	
}
