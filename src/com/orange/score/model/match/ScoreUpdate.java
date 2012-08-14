package com.orange.score.model.match;

import com.orange.score.network.ScoreNetworkRequest;

public class ScoreUpdate {

	public static final int UPDATE_TYPE_SCORE = 0;
	public static final int UPDATE_TYPE_RED_CARD = 1;
	public static final int UPDATE_TYPE_YELLOW_CARD = 2;

	public static final int FLAG_BOTH = 0;
	public static final int FLAG_HOME = 1;
	public static final int FLAG_AWAY = 2;
	
	public String[] scoreUpdateData;
	public 	int		homeAwayFlag;					// event happen in which side
	public 	int		updateType;						// it's score or card event
	
	public 	String	minutes;							// event happen minutes
	public 	String	date;									// match start date
	public 	String	leagueName;						// match league name
	
	public 	String	homeTeamName;
	public 	String	awayTeamName;
	
	public  String	score;
	public  String	redCardScore;
	public  String	yellowCardScore;
	
	public  int			leagueColor;

	public String 	homeTeamScore;
	public String 	awayTeamScore;
	public String 	homeTeamRed;
	public String 	awayTeamRed;
	public String 	homeTeamYellow;
	public String 	awayTeamYellow;
	
	public ScoreUpdate(){
	}
	
	public static boolean isDataChange(String newValue, String oldValue){
		if (oldValue == null || newValue == null)
			return false;
		
		int oldIntValue = 0;
		int newIntValue = 0;
		if (oldValue.length() > 0){
			oldIntValue = Integer.parseInt(oldValue);
		}
		
		if (newValue.length() > 0){
			newIntValue = Integer.parseInt(newValue);
		}
		
		if (newIntValue > oldIntValue){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static ScoreUpdate createScoreUpdateObject(Match match, String[] data){
		if (match == null || data == null || data.length == 0)
			return null;
		
		ScoreUpdate obj = new ScoreUpdate();
		
		boolean homeTeamScoreUpdate = false;
		boolean homeTeamRedUpdate = false;
		boolean homeTeamYellowUpdate = false;
		boolean awayTeamScoreUpdate = false;
		boolean awayTeamRedUpdate = false;
		boolean awayTeamYellowUpdate = false;
				
	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_SCORE], match.homeTeamScore)){
	    	homeTeamScoreUpdate = true;
	    }	    
	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE], match.awayTeamScore)){
	    	awayTeamScoreUpdate = true;
	    }

	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_RED], match.homeTeamRed)){
	    	homeTeamRedUpdate = true;
	    }	    
	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_RED], match.awayTeamRed)){
	    	awayTeamRedUpdate = true;
	    }

	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW], match.homeTeamYellow)){
	    	homeTeamYellowUpdate = true;
	    }	    
	    if (isDataChange(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW], match.awayTeamYellow)){
	    	awayTeamYellowUpdate = true;
	    }
	    
	    if (homeTeamScoreUpdate || awayTeamScoreUpdate){
	    	obj.updateType = UPDATE_TYPE_SCORE;
	    	if (homeTeamScoreUpdate && awayTeamScoreUpdate){
	    		obj.homeAwayFlag = FLAG_BOTH;
	    	}
	    	else if (homeTeamScoreUpdate){
	    		obj.homeAwayFlag = FLAG_HOME;
	    	}
	    	else if (awayTeamScoreUpdate){
	    		obj.homeAwayFlag = FLAG_AWAY;
	    	}	    		
	    }
	    else if (homeTeamRedUpdate || awayTeamRedUpdate){
	    	obj.updateType = UPDATE_TYPE_RED_CARD;	    	
	    	if (homeTeamRedUpdate && awayTeamRedUpdate){
	    		obj.homeAwayFlag = FLAG_BOTH;
	    	}
	    	else if (homeTeamRedUpdate){
	    		obj.homeAwayFlag = FLAG_HOME;
	    	}
	    	else if (awayTeamRedUpdate){
	    		obj.homeAwayFlag = FLAG_AWAY;
	    	}	    		
	    }
	    else if (homeTeamYellowUpdate || awayTeamYellowUpdate){
	    	obj.updateType = UPDATE_TYPE_YELLOW_CARD;	    		    	
	    	if (homeTeamYellowUpdate && awayTeamYellowUpdate){
	    		obj.homeAwayFlag = FLAG_BOTH;
	    	}
	    	else if (homeTeamYellowUpdate){
	    		obj.homeAwayFlag = FLAG_HOME;
	    	}
	    	else if (awayTeamYellowUpdate){
	    		obj.homeAwayFlag = FLAG_AWAY;
	    	}	    		
	    } 
	    else{
	    	// the same as current match data, no update
	    	return null;
	    }
	    
	    obj.score = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_SCORE].
	    						concat(":").
	    						concat(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE]);
	    obj.redCardScore = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_RED].
										concat(":").
										concat(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_RED]);
	    obj.yellowCardScore = data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW].
											concat(":").
											concat(data[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW]);

		obj.scoreUpdateData = data;		
		obj.minutes = match.matchMinuteString();
		obj.homeTeamName = match.homeTeamName;
		obj.awayTeamName = match.awayTeamName;
		obj.date = match.getDateString();
		obj.leagueName = match.getLeagueName();
		obj.leagueColor = match.getLeagueColor();
		
		return obj;
	}
	
}
