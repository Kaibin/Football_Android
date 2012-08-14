package com.orange.score.model.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.R.integer;
import android.util.Log;

import com.orange.score.android.service.FollowMatchService;
import com.orange.score.constants.ScoreType;
import com.orange.score.model.league.League;
import com.orange.score.model.league.LeagueManager;
import com.orange.score.network.ScoreNetworkRequest;
import com.orange.score.service.LiveUpdateChangeCallBack;

public class MatchManager implements LiveUpdateChangeCallBack {
	
	static final String TAG = "MatchManager";

	// store current all match
	List<Match>			matchList = new ArrayList<Match>();
	Map<String, Match>	matchMap = new HashMap<String, Match>();
	
	// store filter conditions
	Set<String>				filterLeagueSet = new HashSet<String>();
	ScoreType				filterScoreType = ScoreType.FIRST;
	FilterMatchStatusType	filterMatchStatus = FilterMatchStatusType.ALL;
	
	public List<Match> getMatchList() {
		return matchList;
	}

	public void setMatchList(List<Match> matchList) {
		this.matchList = matchList;
	}

	public Set<String> getFilterLeagueSet() {
		return filterLeagueSet;
	}

	public void setFilterLeagueSet(Set<String> filterLeagueSet) {
		this.filterLeagueSet = filterLeagueSet;
	}

	public ScoreType getFilterScoreType() {
		return filterScoreType;
	}

	public void setFilterScoreType(ScoreType filterScoreType) {
		
		if (this.filterScoreType != filterScoreType){
			this.clearAllLeagues();
		}
		
		this.filterScoreType = filterScoreType;
	}

	public FilterMatchStatusType getFilterMatchStatus() {
		return filterMatchStatus;
	}

	public void setFilterMatchStatus(FilterMatchStatusType filterMatchStatus) {
		this.filterMatchStatus = filterMatchStatus;
	}
	
	// 返回已经完成比赛
	public List<Match> getFinishedMatch() {
		List<Match>	finishedMatchList = new ArrayList<Match>();
		for(Match match : matchList) {
			if (match.getStatus() == MatchStatusType.FINISH) {
				finishedMatchList.add(match);
			}
		}
		return finishedMatchList;
	}
	
	public List<Match> getUnfinishedMatch() {
		List<Match>	unfinishedMatchList = new ArrayList<Match>();
		for(Match match : matchList) {
			if (match.getStatus() != MatchStatusType.FINISH) {
				unfinishedMatchList.add(match);
			}
		}
		return unfinishedMatchList;
	}
	
	
	public void updateMatchDetail(String matchId, String[] stringList) {
		if (matchId == null || matchId.length() == 0) {
			Log.e(TAG, "update match detail but matchId is empty");
			return;
		}

		Match match = findMatchById(matchId);
		if (match == null){
			Log.d(TAG, "updateMatchDetail but matchId " + matchId + " not found");
			return;
		}
		
		match.updateMatchDetail(stringList);
		
	}

	public void updateDataFromStringList(String[] stringList, LeagueManager leagueManager, FollowMatchManager followMatchManager){
		// clear all old data
		matchList.clear();
		matchMap.clear();

		if (stringList == null || stringList.length == 0)
			return;
		
		for (int i=0; i<stringList.length; i++){			

			String[] fields = stringList[i].split(ScoreNetworkRequest.SEP_FIELD);
			
			if (fields.length < ScoreNetworkRequest.MATCH_FIELD_MIN_COUNT){
				Log.w(TAG, "updateDataFromStringList but field count not enough, count = "
						+ fields.length);
				continue;
			}
			
			Match match = new Match();
			
			String matchId = fields[ScoreNetworkRequest.INDEX_MATCH_ID];
			String leagueId = fields[ScoreNetworkRequest.INDEX_MATCH_LEAGUE_ID];
			int status = Integer.parseInt(fields[ScoreNetworkRequest.INDEX_MATCH_STATUS]);
			String date = fields[ScoreNetworkRequest.INDEX_MATCH_DATE];
			String startDate = fields[ScoreNetworkRequest.INDEX_MATCH_START_DATE];
			String homeTeamName = fields[ScoreNetworkRequest.INDEX_MATCH_HOME_TEAM_NAME];
			String awayTeamName = fields[ScoreNetworkRequest.INDEX_MATCH_AWAY_TEAM_NAME];
			String homeTeamScore = fields[ScoreNetworkRequest.INDEX_MATCH_HOME_TEAM_SCORE];
			String awayTeamScore = fields[ScoreNetworkRequest.INDEX_MATCH_AWAY_TEAM_SCORE];
			String homeTeamFirstHalfScore = fields[ScoreNetworkRequest.INDEX_MATCH_HOME_TEAM_FIRST_HALF_SCORE];
			String awayTeamFirstHalfScore = fields[ScoreNetworkRequest.INDEX_MATCH_AWAY_TEAM_FIRST_HALF_SCORE];
			String homeTeamRed = fields[ScoreNetworkRequest.INDEX_MATCH_HOME_TEAM_RED];
			String awayTeamRed = fields[ScoreNetworkRequest.INDEX_MATCH_AWAY_TEAM_RED];
			String homeTeamYellow = fields[ScoreNetworkRequest.INDEX_MATCH_HOME_TEAM_YELLOW];
			String awayTeamYellow = fields[ScoreNetworkRequest.INDEX_MATCH_AWAY_TEAM_YELLOW];
			
			match.setMatchId(matchId);
			match.setLeagueId(leagueId);
			match.setStatus(status);
			match.setDate(date);
			match.setStartDate(startDate);
			match.setHomeTeamName(homeTeamName);
			match.setAwayTeamName(awayTeamName);
			match.setHomeTeamScore(homeTeamScore);
			match.setAwayTeamScore(awayTeamScore);
			match.setHomeTeamFirstHalfScore(homeTeamFirstHalfScore);
			match.setAwayTeamFirstHalfScore(awayTeamFirstHalfScore);
			match.setHomeTeamRed(homeTeamRed);
			match.setAwayTeamRed(awayTeamRed);
			match.setHomeTeamYellow(homeTeamYellow);
			match.setAwayTeamYellow(awayTeamYellow);	
			
			if (fields.length >= ScoreNetworkRequest.INDEX_MATCH_CROWN_CHUPAN+1){
				match.setCrownChupan(fields[ScoreNetworkRequest.INDEX_MATCH_CROWN_CHUPAN]);
			}
			
			matchList.add(match);
			matchMap.put(matchId, match);
			
			// update league match info (currently it's match counter)
			League league = leagueManager.findLeagueById(leagueId);
			if (league != null){
				match.setLeagueName(league.name);
				league.increaseMatchCount();
			}
			
			// update match follow status
			/*
			boolean isFollow = followMatchManager.isMatchFollowed(matchId);
			match.setIsFollow(isFollow);
			*/
		}
	}

	public List<Match> filterMatch() {
		
		List<Match> retMatchList = new ArrayList<Match>();
		
		switch (filterMatchStatus){
		case FOLLOWED:
			retMatchList = FollowMatchService.getFollowMatchManager().getFollowMatchList();
			return retMatchList;

		case ALL:
		case NOT_STARTED:
		case ONGOING:
		case FINISH:
		default:			
			for (Match match : matchList){
				if (filterMatchStatus == FilterMatchStatusType.ALL || 
					match.getMatchStatusForFilter() == filterMatchStatus){
					if (!filterLeagueSet.contains(match.leagueId))
						continue;
					
					boolean isFollowed = FollowMatchService.getFollowMatchManager().isMatchFollowed(match.matchId);
					match.setIsFollow(isFollowed);
					retMatchList.add(match);
//					Log.d(TAG, "match " + match.toString() + " shown");
				}
				
				Collections.sort(retMatchList);
			}
			break;

		}		
		
		return retMatchList;
	}

	@Override
	public void notifyScoreLiveUpdate(List<String[]> list) {
		int size = list.size();
		for (int i=0; i<size; i++){
			String[] scoreUpdate = list.get(i);
			if (scoreUpdate == null || 
					scoreUpdate.length < ScoreNetworkRequest.REALTIME_SCORE_FILED_COUNT){
				continue;
			}			
			
			String matchId = scoreUpdate[ScoreNetworkRequest.INDEX_REALTIME_SCORE_MATCHID];
			Match match = findMatchById(matchId);
			if (match != null){
				Log.d(TAG, "notifyScoreLiveUpdate, match " + 
						match.toString() + " has live change");
				match.updateDataByLiveChange(scoreUpdate);
			}			
			else{
				Log.d(TAG, "notifyScoreLiveUpdate, match has live change but not found");				
			}
		}
	}
	
	public Match findMatchById(String matchId) {
		return matchMap.get(matchId);
	}

	public void selectAllLeagues(List<League> list) {
		filterLeagueSet.clear();
		for (League league : list){
			filterLeagueSet.add(league.leagueId);
//			Log.d(TAG, "<selectAllLeagues> set = " + filterLeagueSet);
		}
	}
	
	public void clearAllLeagues(){
		filterLeagueSet.clear();
	}

	public void selectLeagues(ArrayList<String> selectedList) {
		filterLeagueSet.clear();
		if (selectedList != null){
			filterLeagueSet.addAll(selectedList);
		}
	}

//	public void followMatch(Match match) {
//		match.setIsFollow(true);
//	}
//
//	public void unfollowMatch(Match match) {
//		match.setIsFollow(false);
//	}
	
}
