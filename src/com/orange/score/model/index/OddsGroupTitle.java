package com.orange.score.model.index;

public class OddsGroupTitle {
	
	private String leagueName;
	private String matchTime;
	private String homeTeamName;
	private String awayTeamName;
	private String finishScore;
	
	public OddsGroupTitle(String leagueName, String matchTime,
			String homeTeamName, String awayTeamName, String finishScore) {
		super();
		this.leagueName = leagueName;
		this.matchTime = matchTime;
		this.homeTeamName = homeTeamName;
		this.awayTeamName = awayTeamName;
		this.finishScore = finishScore;
	}

	public String getFinishScore() {
		return finishScore;
	}

	public void setFinishScore(String finishScore) {
		this.finishScore = finishScore;
	}


	public String getLeagueName() {
		return leagueName;
	}

	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}

	public String getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(String matchTime) {
		this.matchTime = matchTime;
	}

	public String getHomeTeamName() {
		return homeTeamName;
	}

	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}
}
