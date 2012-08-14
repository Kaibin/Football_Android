
package com.orange.score.model.match;

import java.util.Date;

import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;

import com.orange.common.util.StringUtil;
import com.orange.score.model.config.ConfigManager;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.utils.DataUtils;

public class Match implements Comparable<Match>{

	private static final int MATCH_DATE_LENGTH = 14;

	private static final int UNKNOWN_COLOR = -1;
	static final int[] LEAGUE_COLOR = {
		Color.argb(255, 0x51, 0x8e, 0xd2), 
		Color.argb(255, 0xe8, 0x81, 0x1a),
		Color.argb(255, 0x94, 0x97, 0x20),
		Color.argb(255, 0x8d, 0x6d, 0xd6),
		Color.argb(255, 0x53, 0xac, 0x98)};
//		R.color.league_color_2, 
//			R.color.league_color_3, R.color.league_color_4, R.color.league_color_5};
//	
//	<color name="league_color_1">#518ed2</color>
//	<color name="league_color_2">#e8811a</color>
//	<color name="league_color_3">#949720</color>
//	<color name="league_color_4">#8d6dd6</color>
//	<color name="league_color_5">#53ac98</color>	

	private static final String TAG = "Match";
	
	public String matchId;
	String leagueId;
	int	status;
	String date;
	String startDate;
	Date   dateOfStartDate;
	public String homeTeamName;
	public String awayTeamName;
	public String homeTeamScore;
	public String awayTeamScore;
	String homeTeamFirstHalfScore;
	String awayTeamFirstHalfScore;
	public String homeTeamRed;
	public String awayTeamRed;
	public String homeTeamYellow;
	public String awayTeamYellow;
	String crownChupan;
	String crownChupanString = null;
	String leagueName;
	int    leagueColor = UNKNOWN_COLOR;		// unknown
	boolean isFollow = false;

	// for match detail
	String homeCantoneseName;
	String awayCantoneseName;
	String matchTime;
	String homeTeamRank;
	String awayTeamRank;
	String homeTeamImageUrl;
	String awayTeamImageUrl;
	String hasLineup;
	
	public long latestScoreUpdateTime = 0;
	
	public String getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(String leagueId) {
		this.leagueId = leagueId;
	}
	
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	} 

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
		dateOfStartDate = StringUtil.dateFromStringByFormat(startDate, "yyyyMMddHHmmss");
	}
	
	public Date getDateOfStartDate(){
		if (dateOfStartDate == null){
			dateOfStartDate = StringUtil.dateFromStringByFormat(startDate, "yyyyMMddHHmmss");
		}
		
		return dateOfStartDate;
	}

	public String getHomeTeamScore() {
		return homeTeamScore;
	}

	public String getAwayTeamScore() {
		return awayTeamScore;
	}

	public String getHomeTeamFirstHalfScore() {
		return homeTeamFirstHalfScore;
	}

	public void setHomeTeamFirstHalfScore(String homeTeamFirstHalfScore) {
		this.homeTeamFirstHalfScore = homeTeamFirstHalfScore;
	}

	public String getAwayTeamFirstHalfScore() {
		return awayTeamFirstHalfScore;
	}

	public void setAwayTeamFirstHalfScore(String awayTeamFirstHalfScore) {
		this.awayTeamFirstHalfScore = awayTeamFirstHalfScore;
	}

	public String getHomeTeamRed() {
		return homeTeamRed;
	}

	public void setHomeTeamRed(String homeTeamRed) {
		this.homeTeamRed = homeTeamRed;
	}

	public String getAwayTeamRed() {
		return awayTeamRed;
	}

	public void setAwayTeamRed(String awayTeamRed) {
		this.awayTeamRed = awayTeamRed;
	}

	public String getHomeTeamYellow() {
		return homeTeamYellow;
	}

	public void setHomeTeamYellow(String homeTeamYellow) {
		this.homeTeamYellow = homeTeamYellow;
	}

	public String getAwayTeamYellow() {
		return awayTeamYellow;
	}

	public void setAwayTeamYellow(String awayTeamYellow) {
		this.awayTeamYellow = awayTeamYellow;
	}

	public String getCrownChupan() {
		return crownChupan;
	}

	public void setCrownChupan(String crownChupan) {
		this.crownChupan = crownChupan;
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
    
	public String getMatchId() {
		return matchId;
	}
	
	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}	
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {		
		this.status = status;
	}
	
	public String getHomeCantoneseName() {
		return homeCantoneseName;
	}

	public void setHomeCantoneseName(String homeCantoneseName) {
		this.homeCantoneseName = homeCantoneseName;
	}

	public String getAwayCantoneseName() {
		return awayCantoneseName;
	}

	public void setAwayCantoneseName(String awayCantoneseName) {
		this.awayCantoneseName = awayCantoneseName;
	}

	public String getMatchTime() {
		return matchTime;
	}

	public void setMatchTime(String matchTime) {
		this.matchTime = matchTime;
	}

	public String getHomeTeamRank() {
		return homeTeamRank;
	}

	public void setHomeTeamRank(String homeTeamRank) {
		this.homeTeamRank = homeTeamRank;
	}

	public String getAwayTeamRank() {
		return awayTeamRank;
	}

	public void setAwayTeamRank(String awayTeamRank) {
		this.awayTeamRank = awayTeamRank;
	}

	public String getHomeTeamImageUrl() {
		return homeTeamImageUrl;
	}

	public void setHomeTeamImageUrl(String homeTeamImageUrl) {
		this.homeTeamImageUrl = homeTeamImageUrl;
	}

	public String getAwayTeamImageUrl() {
		return awayTeamImageUrl;
	}

	public void setAwayTeamImageUrl(String awayTeamImageUrl) {
		this.awayTeamImageUrl = awayTeamImageUrl;
	}

	public String getHasLineup() {
		return hasLineup;
	}

	public void setHasLineup(String hasLineup) {
		this.hasLineup = hasLineup;
	}

	public FilterMatchStatusType getMatchStatusForFilter() {
		
		switch (status){
		
		case MatchStatusType.FIRST_HALF:
        case MatchStatusType.MIDDLE:
        case MatchStatusType.SECOND_HALF:
        case MatchStatusType.PAUSE:
        case MatchStatusType.OVERTIME:
            return FilterMatchStatusType.ONGOING;
            
        case MatchStatusType.FINISH:
        case MatchStatusType.TBD:
        case MatchStatusType.KILL:
        case MatchStatusType.POSTPONE:
        case MatchStatusType.CANCEL:
            return FilterMatchStatusType.FINISH;
            
        case MatchStatusType.NOT_STARTED:
        default:
            return FilterMatchStatusType.NOT_STARTED;		
		}		
	}
	
	public String getStatusString() {
		switch (status) {
		case MatchStatusType.NOT_STARTED:
			return "未开";
		case MatchStatusType.FIRST_HALF:
			return "上半场";
		case MatchStatusType.MIDDLE:
			return "中场";
		case MatchStatusType.SECOND_HALF:
			return "下半场";
		case MatchStatusType.OVERTIME:
			return "加";
		case MatchStatusType.TBD:
			return "待定";
		case MatchStatusType.KILL:
			return "腰斩";
		case MatchStatusType.PAUSE:
			return "中断";
		case MatchStatusType.POSTPONE:
			return "推迟";
		case MatchStatusType.FINISH:
			return "完场";
		case MatchStatusType.CANCEL:
			return "取消";
		default:
			return "未开";
		}
	}

	public String toLongString() {
		return "Match [awayCantoneseName=" + awayCantoneseName
				+ ", awayTeamFirstHalfScore=" + awayTeamFirstHalfScore
				+ ", awayTeamImageUrl=" + awayTeamImageUrl + ", awayTeamName="
				+ awayTeamName + ", awayTeamRank=" + awayTeamRank
				+ ", awayTeamRed=" + awayTeamRed + ", awayTeamScore="
				+ awayTeamScore + ", awayTeamYellow=" + awayTeamYellow
				+ ", crownChupan=" + crownChupan + ", crownChupanString="
				+ crownChupanString + ", date=" + date + ", dateOfStartDate="
				+ dateOfStartDate + ", hasLineup=" + hasLineup
				+ ", homeCantoneseName=" + homeCantoneseName
				+ ", homeTeamFirstHalfScore=" + homeTeamFirstHalfScore
				+ ", homeTeamImageUrl=" + homeTeamImageUrl + ", homeTeamName="
				+ homeTeamName + ", homeTeamRank=" + homeTeamRank
				+ ", homeTeamRed=" + homeTeamRed + ", homeTeamScore="
				+ homeTeamScore + ", homeTeamYellow=" + homeTeamYellow
				+ ", isFollow=" + isFollow + ", leagueColor=" + leagueColor
				+ ", leagueId=" + leagueId + ", leagueName=" + leagueName
				+ ", matchId=" + matchId + ", matchTime=" + matchTime
				+ ", startDate=" + startDate + ", status=" + status + "]";
	}
	
	@Override
	public String toString() {
		return "[matchId=" + matchId + ", home=" + homeTeamName + ", away=" + awayTeamName + "]";
	}
	
	private int safeGetInt(String string){
		return (string == null || string.length() == 0) ? 0 : Integer.parseInt(string);
	}

	public void setStatus(String string) {
		this.status = safeGetInt(string);
	}

	public void setHomeTeamScore(String homeTeamScore) {
		this.homeTeamScore = homeTeamScore;
	}

	public void setAwayTeamScore(String awayTeamScore) {
		this.awayTeamScore = awayTeamScore;
	}

	public void updateDataByLiveChange(String[] scoreUpdate) {		
		this.setStatus(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_STATUS]);
		this.setDate(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_DATE]);
		this.setStartDate(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_START_DATE]);
		this.setHomeTeamScore(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_SCORE]);
		this.setAwayTeamScore(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_SCORE]);
		this.setHomeTeamFirstHalfScore(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_FIRST_HALF_SCORE]);
		this.setAwayTeamFirstHalfScore(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_FIRST_HALF_SCORE]);
		this.setHomeTeamRed(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_RED]);
		this.setAwayTeamRed(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_RED]);
		this.setHomeTeamYellow(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_HOME_TEAM_YELLOW]);
		this.setAwayTeamYellow(scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_AWAY_TEAM_YELLOW]);
	}

	public String getLeagueName() {
		return leagueName;
	}

	public String getDateString() {
		if (date != null && date.length() >= MATCH_DATE_LENGTH){
			return date.substring(8, 10) + ":" + date.substring(10, 12);
		}
		else{
			return "";
		}
	}

	public String getMatchStatusString() {
	    switch (status) {
        case MatchStatusType.NOT_STARTED:
            return "未开";
        case MatchStatusType.FIRST_HALF:
            return "上半场";
        case MatchStatusType.MIDDLE:
            return "中";
        case MatchStatusType.SECOND_HALF:
            return "下半场";
        case MatchStatusType.TBD:
            return "待定";
        case MatchStatusType.KILL:
            return "腰斩";
        case MatchStatusType.PAUSE:
            return "中断";
        case MatchStatusType.POSTPONE:
            return "推迟";
        case MatchStatusType.OVERTIME:
            return "加";            
        case MatchStatusType.FINISH:
            return "完";
        case MatchStatusType.CANCEL:
            return "取消";
        default:
            return "未开";
	    }
	}

	public boolean hasFirstHalfScore() {
		return (homeTeamFirstHalfScore != null && homeTeamFirstHalfScore.length() > 0
				&& awayTeamFirstHalfScore != null && awayTeamFirstHalfScore.length() > 0) ? 
						true : false;
	}
	
	private boolean hasCard(String cardValue){
		return (cardValue != null && cardValue.length() > 0 
				&& Integer.parseInt(cardValue) > 0 ) 
				? true : false;
	}

	public boolean hasAwayTeamYellow() {
		return hasCard(awayTeamYellow);
	}
	
	public boolean hasHomeTeamYellow() {
		return hasCard(homeTeamYellow);
	}

	public boolean hasAwayTeamRed() {
		return hasCard(awayTeamRed);
	}

	public boolean hasHomeTeamRed() {
		return hasCard(homeTeamRed);
	}
	
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}
	
	public boolean hasChupan() {
		return (this.crownChupan != null && this.crownChupan.length() > 0) ? true : false;
	}

	public String getCrownChupanString() {
		if (this.crownChupanString == null){
			this.crownChupanString = DataUtils.toChuPanString(this.crownChupan);
		}
		
		return this.crownChupanString;
	}

	public int getLeagueColor() {
		if (leagueColor != UNKNOWN_COLOR)
			return leagueColor;
				
		int index = Integer.parseInt(leagueId) % LEAGUE_COLOR.length;
		leagueColor = LEAGUE_COLOR[index];
		return leagueColor;
	}

	public boolean needDisplayScore() {
		
		switch (status){
        case MatchStatusType.NOT_STARTED:
        case MatchStatusType.TBD:
        case MatchStatusType.KILL:
        case MatchStatusType.POSTPONE:
        case MatchStatusType.CANCEL:
            return false;
            
        case MatchStatusType.FIRST_HALF:
        case MatchStatusType.MIDDLE:
        case MatchStatusType.SECOND_HALF:
        case MatchStatusType.PAUSE:
        case MatchStatusType.OVERTIME:
        case MatchStatusType.FINISH:
            return true;
            
        default:
            return false;		
		}
		
	}
	
	public int getMatchStatusColor(){
		switch (status){
            
        case MatchStatusType.FIRST_HALF:
        case MatchStatusType.SECOND_HALF:
        case MatchStatusType.OVERTIME:
        	return Color.argb(255, 0xff, 0x33, 0x00);
        	
        case MatchStatusType.MIDDLE:
        case MatchStatusType.PAUSE:
        	return Color.argb(255, 0x42, 0x95, 0xdf);
        	
        case MatchStatusType.FINISH:
        case MatchStatusType.TBD:
        case MatchStatusType.KILL:
        case MatchStatusType.POSTPONE:
        case MatchStatusType.CANCEL:
            return Color.RED;
            
        case MatchStatusType.NOT_STARTED:
        default:
            return Color.GRAY;
		}
	}

	public int getMatchScoreColor() {
		switch (status){
        
        case MatchStatusType.FIRST_HALF:
        case MatchStatusType.SECOND_HALF:
        case MatchStatusType.PAUSE:
        case MatchStatusType.OVERTIME:
        	return Color.argb(255, 0x33, 0x99, 0x00);
        	
        case MatchStatusType.MIDDLE:
        case MatchStatusType.POSTPONE:
        	return Color.argb(255, 0x42, 0x95, 0xdf);
        	
        case MatchStatusType.FINISH:
        case MatchStatusType.TBD:
        case MatchStatusType.KILL:
        case MatchStatusType.CANCEL:
            return Color.RED;
            
        case MatchStatusType.NOT_STARTED:
        default:
            return Color.GRAY;
		}
	}

	public boolean isFollow() {
		return isFollow;
	}

	public void setIsFollow(boolean b) {
		isFollow = b;
	}

	public String matchMinuteString(){
		Date calcStartDate = getDateOfStartDate();
		Date now = new Date();
		long seconds = 0;
		switch (status){
        case MatchStatusType.FIRST_HALF:
        	seconds = now.getTime()/1000 - calcStartDate.getTime()/1000
        			+ ConfigManager.getServerDifferenceTime();
        	if (seconds > 45*60)
        		return "45+";
        	else
        		return String.valueOf(seconds/60).concat("'");
        case MatchStatusType.SECOND_HALF:
        	seconds = now.getTime()/1000 - calcStartDate.getTime()/1000
        			+ ConfigManager.getServerDifferenceTime() + 45*60;
        	if (seconds > 90*60)
        		return "90+";
        	else
        		return String.valueOf(seconds/60).concat("'");
        default:
        	return "";
		}
		
		
	}
	
	public String getMatchStatusOrMinutString() {
		switch (status) {
        case MatchStatusType.FIRST_HALF:
        case MatchStatusType.SECOND_HALF:
        	return matchMinuteString();
            
        case MatchStatusType.MIDDLE:
        case MatchStatusType.TBD:
        case MatchStatusType.KILL:
        case MatchStatusType.PAUSE:
        case MatchStatusType.POSTPONE:
        case MatchStatusType.OVERTIME:
        case MatchStatusType.FINISH:
        case MatchStatusType.CANCEL:
        default:
            return getMatchStatusString();
	    }	
	}

	public void fromCursor(Cursor cursor) {

		int index = 0;
		
		matchId = cursor.getString(index++);
		leagueId = cursor.getString(index++);
		status = cursor.getInt(index++);
		date = cursor.getString(index++);
		startDate = cursor.getString(index++);
		homeTeamName = cursor.getString(index++);
		awayTeamName = cursor.getString(index++);
		homeTeamScore = cursor.getString(index++);
		awayTeamScore = cursor.getString(index++);
		homeTeamFirstHalfScore = cursor.getString(index++);
		awayTeamFirstHalfScore = cursor.getString(index++);
		homeTeamRed = cursor.getString(index++);
		awayTeamRed = cursor.getString(index++);
		homeTeamYellow = cursor.getString(index++);
		awayTeamYellow = cursor.getString(index++);
		crownChupan = cursor.getString(index++);
		leagueName = cursor.getString(index++);
		isFollow = (cursor.getInt(index++) == 1);
		homeCantoneseName = cursor.getString(index++);
		awayCantoneseName = cursor.getString(index++);
		homeTeamRank = cursor.getString(index++);
		awayTeamRank= cursor.getString(index++);
		homeTeamImageUrl = cursor.getString(index++);
		awayTeamImageUrl = cursor.getString(index++);
		hasLineup = cursor.getString(index++);
		
		Log.d(TAG, "get match from cursor, match = " + toString());
	}

	@Override
	public int compareTo(Match otherMatch) {
		
		if (this.status == otherMatch.status){
			return this.date.compareTo(otherMatch.date);
		}
		else{
			boolean finished = this.isFinish();
			boolean otherMatchFinished = otherMatch.isFinish();
			if (finished && !otherMatchFinished){
				return 1;
			}
			else if (!finished && otherMatchFinished){
				return -1;
			}
			else{
				return this.date.compareTo(otherMatch.date);
			}		
		}
	}

	public boolean isFinish() {
		switch (status) {
		case MatchStatusType.NOT_STARTED:
        case MatchStatusType.FIRST_HALF:
        case MatchStatusType.SECOND_HALF:            
        case MatchStatusType.MIDDLE:
        case MatchStatusType.PAUSE:
        case MatchStatusType.OVERTIME:
        	return false;

        case MatchStatusType.TBD:
        case MatchStatusType.KILL:
        case MatchStatusType.POSTPONE:
        case MatchStatusType.FINISH:
        case MatchStatusType.CANCEL:
        default:
        	return true;
		}	
	}

	public void updateMatchDetail(String[] stringList) {
		if (stringList == null || stringList.length == 0) {
			return;
		}
		 
		this.setHomeCantoneseName(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_HOME_CANTONESE_NAME]);
		this.setAwayCantoneseName(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_AWAY_CANTONESE_NAME]);
		this.setMatchTime(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_TIME]);
		this.setStatus(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_STATUS]);
		this.setHomeTeamScore(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_HOME_SCORE]);
		this.setAwayTeamScore(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_AWAY_SCORE]);
		this.setHomeTeamRank(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_HOME_RANK]);
		this.setAwayTeamRank(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_AWAY_RANK]);
		this.setHomeTeamImageUrl(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_HOME_IMAGE_URL]);
		this.setAwayTeamImageUrl(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_AWAY_IMAGE_URL]);
		// match.setHasLineup(stringList[ScoreNetworkRequest.INDEX_REALTIME_MATCH_HAS_LINEUP]);
	}

	static int MIN_SCORE_UPDATE_INTERVAL = 10*1000;
	public boolean isScoreJustUpdate() {
		return (System.currentTimeMillis() - latestScoreUpdateTime <= MIN_SCORE_UPDATE_INTERVAL) ? true : false;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Match))
	           return false;
		Match object = (Match) obj;
		
		return matchId.equals(matchId)
			&& leagueName.equals(object.leagueName) 
			&& homeTeamName.equals(object.homeTeamName)
			&& awayTeamName.equals(object.awayTeamName);
	}
	
	@Override
	public int hashCode() {
		return matchId.hashCode() + leagueName.hashCode() + homeTeamName.hashCode() + awayTeamName.hashCode();
	}
	
}
